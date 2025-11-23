
import com.google.gson.Gson;
import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class RhythmGame {

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java -cp .;gson-2.x.x.jar RhythmGame <beatmap.json>");
            return;
        }

        SwingUtilities.invokeLater(() -> {
            try {
                JFrame frame = new JFrame("Ambatusong");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setResizable(false);

                GamePanel panel = new GamePanel();
                frame.add(panel);
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);

                panel.initializeGame(args[0]);

            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
            }
        });
    }

    // ==========================================
    // SFX MANAGER
    // ==========================================
    static class SFXManager {
        private static final Map<String, Clip> sounds = new HashMap<>();

        public static void loadSounds() {
            load("normal", "sfx/soft-hitnormal.wav");
            load("clap",   "sfx/soft-hitclap.wav");
            load("finish", "sfx/soft-hitfinish.wav");
        }

        private static void load(String name, String path) {
            try {
                File f = new File(path);
                if (!f.exists()) { System.out.println("Warning: SFX file missing: " + path); return; }
                AudioInputStream ais = AudioSystem.getAudioInputStream(f);
                Clip clip = AudioSystem.getClip(); clip.open(ais);
                sounds.put(name, clip);
            } catch (Exception e) { System.err.println("Error loading SFX " + name + ": " + e.getMessage()); }
        }

        public static void play(String name) {
            Clip c = sounds.get(name);
            if (c != null) { if (c.isRunning()) c.stop(); c.setFramePosition(0); c.start(); }
        }
    }

    // ==========================================
    // MODEL
    // ==========================================

    enum Judgement {
        PERFECT(300, 0.05), GOOD(100, 0.12), MISS(0, 0.5);
        final int score; final double window;
        Judgement(int score, double window) { this.score = score; this.window = window; }
    }

    static class BeatmapData {
        String title; String audioFile; String background;
        int lanes = 4; double approachTime = 2.0;
        List<NoteJSON> notes;
    }

    static class NoteJSON { double time; int lane; String type; double length; }

    abstract static class Note {
        protected final int lane; protected final double time;
        protected boolean isHit = false; protected boolean isMissed = false;
        public Note(int lane, double time) { this.lane = lane; this.time = time; }
        public int getLane() { return lane; } public double getTime() { return time; }
        public boolean isHit() { return isHit; } public boolean isMissed() { return isMissed; }
        public abstract void update(double currentTime, ScoreManager scoreManager);
        public abstract void onKeyPressed(double diff, ScoreManager scoreManager);
        public abstract void onKeyReleased(double currentTime, ScoreManager scoreManager);
    }

    static class TapNote extends Note {
        public TapNote(int lane, double time) { super(lane, time); }
        @Override public void update(double currentTime, ScoreManager sm) {
            if (!isHit && !isMissed && currentTime > time + Judgement.MISS.window) {
                isMissed = true; sm.addScore(Judgement.MISS);
            }
        }
        @Override public void onKeyPressed(double diff, ScoreManager sm) {
            double abs = Math.abs(diff);
            // PERUBAHAN: Kita HAPUS SFXManager.play("normal") disini agar tidak dobel bunyi.
            // Bunyi sudah dihandle di GameEngine.
            if (abs <= Judgement.PERFECT.window) { isHit = true; sm.addScore(Judgement.PERFECT); } 
            else if (abs <= Judgement.GOOD.window) { isHit = true; sm.addScore(Judgement.GOOD); } 
            else if (abs <= Judgement.MISS.window) { isHit = true; }
        }
        @Override public void onKeyReleased(double t, ScoreManager s) {}
    }

    static class HoldNote extends Note {
        private final double length;
        public boolean isHolding = false;
        public boolean completed = false;

        public HoldNote(int lane, double time, double length) {
            super(lane, time);
            this.length = length;
        }
        
        public double getLength() { return length; }

        @Override
        public void update(double currentTime, ScoreManager sm) {
            if (!isHolding && !isHit && !isMissed && currentTime > time + Judgement.MISS.window) {
                isMissed = true;
                sm.addScore(Judgement.MISS);
            }
        }

        @Override
        public void onKeyPressed(double diff, ScoreManager sm) {
            if (Math.abs(diff) <= Judgement.MISS.window) {
                isHolding = true;
            }
        }

        @Override
        public void onKeyReleased(double currentTime, ScoreManager sm) {
            if (isHolding) {
               
                if (currentTime >= time + length - 0.2) {
                    completed = true; 
                    isHit = true; 
                    sm.addScore(Judgement.PERFECT);
                    SFXManager.play("finish"); 
                } else {
                    isMissed = true;
                    sm.addScore(Judgement.MISS);
                }
                isHolding = false;
            }
        }
    }

    // ==========================================
    // CONTROLLER
    // ==========================================

    static class AudioController {
        private Clip clip;
        public void load(String path) throws Exception {
            File audioFile = new File(path);
            if (!audioFile.exists()) throw new FileNotFoundException("Audio not found: " + path);
            AudioInputStream ais = AudioSystem.getAudioInputStream(audioFile);
            clip = AudioSystem.getClip(); clip.open(ais);
        }
        public void play() { if (clip != null) { clip.setFramePosition(0); clip.start(); } }
        public void stop() { if (clip != null) clip.stop(); }
        public double getPosition() { return (clip == null) ? 0 : clip.getMicrosecondPosition() / 1_000_000.0; }
        public boolean isPlaying() { return clip != null && clip.isRunning(); }
    }

    static class ScoreManager {
        private int score = 0; private int combo = 0;
        public void addScore(Judgement j) {
            if (j == Judgement.MISS) combo = 0; else { combo++; score += j.score * (1 + (combo / 100)); }
            System.out.println("Judgement: " + j + " | Score: " + score + " | Combo: " + combo);
        }
        public int getScore() { return score; } public int getCombo() { return combo; }
    }

    static class InputController implements KeyListener {
        private final GameEngine engine;
        private final Map<Integer, Integer> keyToLaneMap;
        private final boolean[] keysHeld;
        public InputController(GameEngine engine, int lanes) {
            this.engine = engine; this.keysHeld = new boolean[lanes]; this.keyToLaneMap = new HashMap<>();
            int[] codes = {KeyEvent.VK_S, KeyEvent.VK_D, KeyEvent.VK_J, KeyEvent.VK_K, KeyEvent.VK_L};
            for(int i=0; i<lanes && i<codes.length; i++) keyToLaneMap.put(codes[i], i);
        }
        
        @Override
        public void keyPressed(KeyEvent e) {
            Integer lane = keyToLaneMap.get(e.getKeyCode());
            if (lane != null) {
          
                if (!keysHeld[lane]) {
                    keysHeld[lane] = true;     
                    engine.handlePress(lane);   
                }
            } else {
                if (!engine.isRunning()) engine.start();
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            Integer lane = keyToLaneMap.get(e.getKeyCode());
            if (lane != null) { keysHeld[lane] = false;
                engine.handleRelease(lane); }
        }

        @Override
        public void keyTyped(KeyEvent e) {}
        public boolean isLanePressed(int lane) { return lane >= 0 && lane < keysHeld.length && keysHeld[lane]; }
    }

    static class GameEngine {
        private final List<Note> notes = new CopyOnWriteArrayList<>();
        private final AudioController audio = new AudioController();
        private final ScoreManager scoreManager = new ScoreManager();
        private boolean running = false;
        private double startTime = 0;
        private int lanes = 4;
        private double approachTime = 2.0;
        private String backgroundPath; 

        public void loadBeatmap(String jsonPath) throws Exception {
            SFXManager.loadSounds(); 
            Gson gson = new Gson();
            BeatmapData bm = gson.fromJson(new FileReader(jsonPath), BeatmapData.class);
            this.lanes = bm.lanes;
            this.approachTime = bm.approachTime;
            
            File jsonFile = new File(jsonPath);
            if (bm.background != null && !bm.background.isEmpty()) {
                this.backgroundPath = new File(jsonFile.getParent(), bm.background).getAbsolutePath();
            }

            String audioPath = new File(jsonFile.getParent(), bm.audioFile).getAbsolutePath();
            try { audio.load(audioPath); } catch (Exception e) { System.err.println("Warning: Audio load failed."); }

            notes.clear();
            for (NoteJSON n : bm.notes) {
                if ("hold".equalsIgnoreCase(n.type)) notes.add(new HoldNote(n.lane, n.time, n.length));
                else notes.add(new TapNote(n.lane, n.time));
            }
        }

        public void start() { running = true; audio.play(); startTime = System.nanoTime() / 1_000_000_000.0; }
        public void update() { if (!running) return; for (Note n : notes) n.update(getAudioTime(), scoreManager); }
        
      
        public void handlePress(int lane) {
            if (!running) return;

            SFXManager.play("normal"); 

      
            double now = getAudioTime(); Note closest = null; double minDiff = Double.MAX_VALUE;
            for (Note n : notes) { if (n.lane == lane && !n.isHit && !n.isMissed) { double diff = n.time - now; if (Math.abs(diff) < minDiff) { minDiff = Math.abs(diff); closest = n; }}}
            if (closest != null) closest.onKeyPressed(closest.time - now, scoreManager);
        }
        
        public void handleRelease(int lane) {
            if (!running) return; for (Note n : notes) if (n.lane == lane && n instanceof HoldNote) n.onKeyReleased(getAudioTime(), scoreManager);
        }
        public double getAudioTime() { return audio.isPlaying() ? audio.getPosition() : (System.nanoTime() / 1e9) - startTime; }
        public boolean isRunning() { return running; }
        public List<Note> getNotes() { return notes; }
        public int getLanes() { return lanes; }
        public double getApproachTime() { return approachTime; }
        public int getScore() { return scoreManager.getScore(); }
        public int getCombo() { return scoreManager.getCombo(); }
        public String getBackgroundPath() { return backgroundPath; } 
    }

    // ==========================================
    // VIEW: Rendering
    // ==========================================

    static class GamePanel extends JPanel implements Runnable {
        private final int WIDTH = 1000, HEIGHT = 600, NOTE_WIDTH = 60, HIT_Y = HEIGHT - 100;
        private GameEngine engine;
        private InputController input;
        private final Color[] LANE_COLORS = { Color.RED, Color.CYAN, Color.GREEN, Color.YELLOW, Color.MAGENTA };
        private Image backgroundImage; 

        public GamePanel() {
            setPreferredSize(new Dimension(WIDTH, HEIGHT));
            setBackground(Color.BLACK);
            setFocusable(true);
            engine = new GameEngine();
        }

        public void initializeGame(String path) {
            try {
                engine.loadBeatmap(path);
                if (engine.getBackgroundPath() != null) {
                    backgroundImage = Toolkit.getDefaultToolkit().createImage(engine.getBackgroundPath());
                }
                input = new InputController(engine, engine.getLanes());
                addKeyListener(input);
                new Thread(this).start();
            } catch (Exception e) { e.printStackTrace(); }
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (backgroundImage != null) {
                g2.drawImage(backgroundImage, 0, 0, WIDTH, HEIGHT, this);
                g2.setColor(new Color(0, 0, 0, 150)); 
                g2.fillRect(0, 0, WIDTH, HEIGHT);
            }

            if (engine == null) return;
            drawLanes(g2); drawNotes(g2); drawUI(g2);
        }

        private void drawLanes(Graphics2D g) {
            int lanes = engine.getLanes();
            int startX = (WIDTH - (lanes * NOTE_WIDTH)) / 2;
            g.setColor(Color.WHITE); g.setStroke(new BasicStroke(3));
            g.drawLine(startX, HIT_Y, startX + lanes * NOTE_WIDTH, HIT_Y);

            for (int i = 0; i < lanes; i++) {
                int x = startX + i * NOTE_WIDTH;
                g.setColor(new Color(30, 30, 30, 100)); g.fillRect(x, 0, NOTE_WIDTH, HEIGHT);
                g.setColor(Color.GRAY); g.drawRect(x, 0, NOTE_WIDTH, HEIGHT);
                if (input != null && input.isLanePressed(i)) {
                    g.setColor(new Color(255, 255, 255, 50)); g.fillRect(x, 0, NOTE_WIDTH, HEIGHT);
                    g.setColor(LANE_COLORS[i % LANE_COLORS.length]); g.fillOval(x + 5, HIT_Y - 10, NOTE_WIDTH - 10, 20);
                }
            }
        }

        private void drawNotes(Graphics2D g) {
            double currentTime = engine.getAudioTime();
            double approachTime = engine.getApproachTime();
            int lanes = engine.getLanes();
            int startX = (WIDTH - (lanes * NOTE_WIDTH)) / 2;
            double speed = HIT_Y / approachTime; 

            for (Note n : engine.getNotes()) {
             
                if (n.isMissed()) continue; 
                
             
                if (n instanceof TapNote && n.isHit()) continue; 
                
               
                if (n instanceof HoldNote && ((HoldNote)n).completed) continue; 

              
                double timeDiff = n.getTime() - currentTime;
             
                if (!(n instanceof HoldNote) && (timeDiff > approachTime || timeDiff < -1.0)) continue;

               
                int x = startX + n.getLane() * NOTE_WIDTH;
                int y = (int) (HIT_Y - (timeDiff * speed));
                Color c = LANE_COLORS[n.getLane() % LANE_COLORS.length];

               
                if (n instanceof HoldNote) {
                    HoldNote hn = (HoldNote) n;
                    int tailLength = (int) (hn.getLength() * speed);
                    int tailY = y - tailLength; 
                    
                  
                    if (hn.isHolding) {
                      
                         y = HIT_Y;
                        
                         double endTime = hn.getTime() + hn.getLength();
                         double remainingTime = endTime - currentTime;
                         
                       
                         tailLength = (int) (remainingTime * speed);
                         
                         if (tailLength < 0) tailLength = 0;
                         
                         tailY = y - tailLength;
                    }
                    
                    g.setColor(c.darker()); 
                    g.fillRect(x + 15, tailY, NOTE_WIDTH - 30, tailLength);
                    g.setColor(c); 
                    g.drawRect(x + 15, tailY, NOTE_WIDTH - 30, tailLength);
                }

                // --- GAMBAR KEPALA NOTE ---
                g.setColor(c); 
                g.fillRoundRect(x + 5, y - 10, NOTE_WIDTH - 10, 20, 10, 10);
                
                g.setColor(Color.WHITE); 
                g.setStroke(new BasicStroke(2)); 
                g.drawRoundRect(x + 5, y - 10, NOTE_WIDTH - 10, 20, 10, 10);
            }
        }

        private void drawUI(Graphics2D g) {
            g.setColor(Color.WHITE); g.setFont(new Font("Arial", Font.BOLD, 20));
            g.drawString("Score: " + engine.getScore(), 20, 30);
            g.drawString("Combo: " + engine.getCombo(), 20, 60);
            if (!engine.isRunning()) {
                String msg = "Press any key to start";
                int w = g.getFontMetrics().stringWidth(msg);
                g.drawString(msg, (WIDTH - w) / 2, HEIGHT / 2);
            }
        }

        @Override
        public void run() {
            while (true) {
                engine.update(); repaint();
                try { Thread.sleep(16); } catch (InterruptedException e) { break; }
            }
        }
    }
}

