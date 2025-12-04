import com.google.gson.Gson;
import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


public class RhythmGame extends JPanel implements Runnable {
    
    private final int WIDTH = 1000, HEIGHT = 600, NOTE_WIDTH = 60, HIT_Y = HEIGHT - 90;
    private final Color[] LANE_COLORS = { Color.RED, Color.CYAN, Color.GREEN, Color.YELLOW, Color.MAGENTA };
    
    private GameEngine engine;
    private InputController input;
    private Image backgroundImage;
    private Thread gameThread;

    private long introStartTime = 0; 
    private boolean engineStarted = false;
    
    
    public void start(String beatmapPath) {
    if (engine != null) {
        engine.stop(); 
        engine = null; 
    }

    if (gameThread != null) {
       
        Thread oldThread = gameThread; 
        gameThread = null; 
        oldThread.interrupt(); 
    }

    engine = new GameEngine(); 
    try {
        engine.loadBeatmap(beatmapPath);
        
        if (engine.getBackgroundPath() != null) {
            backgroundImage = Toolkit.getDefaultToolkit().createImage(engine.getBackgroundPath());
        } else {
            backgroundImage = null;
        }

        for(KeyListener k : getKeyListeners()) removeKeyListener(k);
        
        input = new InputController(engine, engine.getLanes());
        addKeyListener(input);
        
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    stopGame(); 
                    Main.showCard("SONG_SELECT"); 
                }
            }
        });

        engineStarted = false; 
        
        introStartTime = 0; 

        gameThread = new Thread(this);
        gameThread.start();
        
        this.requestFocusInWindow();


    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        Main.showCard("SONG_SELECT");
    }
}   

    public void stopGame() {
    
    Thread moribund = gameThread;
    gameThread = null; 
    if (moribund != null) {
        moribund.interrupt();
    }

    if (engine != null) {
        engine.stop(); 
    }
    
    engineStarted = false;
}

 @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        if (backgroundImage != null) {
            g2.drawImage(backgroundImage, 0, 0, w, h, this);
            g2.setColor(new Color(0, 0, 0, 150)); 
            g2.fillRect(0, 0, w, h);
        }

        if (engine == null) return;

        drawLanes(g2);
        
        if (engineStarted) {
            drawNotes(g2);
        }
        
        drawUI(g2);

        double time = (System.currentTimeMillis() - introStartTime) / 1000.0;

        if (time < 3.0) {
            String text = "";
            Color textColor = Color.WHITE;
            if (time >= 0.0 && time < 1.0) {
                text = "AMBA"; textColor = Color.CYAN;
            } 
            else if (time >= 1.0 && time < 2.0) {
                text = "TU"; textColor = Color.YELLOW;
            } 
            else if (time >= 2.0 && time < 3.0) {
                text = "SONG"; textColor = Color.MAGENTA;
            }
            
                  
            if (!text.isEmpty()) {
                g2.setFont(new Font("Arial", Font.BOLD, 150));
                FontMetrics fm = g2.getFontMetrics();
                int textW = fm.stringWidth(text);
                int textH = fm.getAscent();
                
                int x = (w - textW) / 2;
                int y = (h + textH) / 2 - 20;
                
                g2.setColor(Color.BLACK);
                g2.drawString(text, x + 5, y + 5); 
                g2.setColor(textColor);
                g2.drawString(text, x, y);        
            }
        }
    }

    // ==========================================
    // SFX MANAGER
    // ==========================================
    static class SFXManager {

        private static final Map<String, List<Clip>> soundPools = new HashMap<>();
        private static final int POOL_SIZE = 4; 

        public static void loadSounds() {

            load("normal", "sfx/soft-hitnormal.wav");
            load("clap",   "sfx/soft-hitclap.wav");
            load("finish", "sfx/soft-hitfinish.wav");
            load("failed", "sfx/combobreak.wav");
            load("start", "sfx/ambatukams.wav");
        }

        private static void load(String name, String path) {
            try {
                File f = new File(path);
                if (!f.exists()) return;
                

                AudioInputStream ais = AudioSystem.getAudioInputStream(f);
                AudioFormat format = ais.getFormat();
                

                DataLine.Info info = new DataLine.Info(Clip.class, format);
                byte[] audioBytes = new byte[(int) (ais.getFrameLength() * format.getFrameSize())];
                ais.read(audioBytes);
                
                List<Clip> pool = new ArrayList<>();
                for (int i = 0; i < POOL_SIZE; i++) {
                    Clip clip = (Clip) AudioSystem.getLine(info);
                    clip.open(format, audioBytes, 0, audioBytes.length);
                    pool.add(clip);
                }
                soundPools.put(name, pool);
                
            } catch (Exception e) {
                System.err.println("Error loading SFX " + name + ": " + e.getMessage());
            }
        }

        public static void play(String name) {
            List<Clip> pool = soundPools.get(name);
            if (pool != null) {

                for (Clip c : pool) {
                    if (!c.isRunning()) {
                        c.setFramePosition(0);
                        c.start();
                        return; 
                    }
                }
                
                Clip c = pool.get(0);
                c.stop();
                c.setFramePosition(0);
                c.start();
            }
        }
    }
    // ==========================================
    // MODEL
    // ==========================================

    enum Judgement {
        PERFECT(300, 0.05), GOOD(100, 0.12), MISS(0, 0.5);

        final int score;
        final double window;

        Judgement(int score, double window) {
            this.score = score;
            this.window = window;
        }
    }

    static class BeatmapData {
        String title;
        String audioFile;
        String background;
        int lanes = 4;
        double approachTime = 2.0;
        List<NoteJSON> notes;
    }

    static class NoteJSON {
        double time;
        int lane;
        String type;
        double length;
    }

    abstract static class Note {
        protected final int lane;
        protected final double time;
        protected boolean isHit = false;
        protected boolean isMissed = false;

        public Note(int lane, double time) {
            this.lane = lane;
            this.time = time;
        }

        public int getLane() {
            return lane;
        }

        public double getTime() {
            return time;
        }

        public boolean isHit() {
            return isHit;
        }

        public boolean isMissed() {
            return isMissed;
        }

        public abstract void update(double currentTime, ScoreManager scoreManager);

        public abstract void onKeyPressed(double diff, ScoreManager scoreManager);

        public abstract void onKeyReleased(double currentTime, ScoreManager scoreManager);
    }

    static class TapNote extends Note {
        public TapNote(int lane, double time) {
            super(lane, time);
        }

        @Override
        public void update(double currentTime, ScoreManager sm) {
            if (!isHit && !isMissed && currentTime > time + Judgement.MISS.window) {
                isMissed = true;
                sm.addScore(Judgement.MISS);
                SFXManager.play("failed");
            }
        }

        @Override
        public void onKeyPressed(double diff, ScoreManager sm) {
            double abs = Math.abs(diff);

            if (abs <= Judgement.PERFECT.window) {
                isHit = true;
                sm.addScore(Judgement.PERFECT);
            } else if (abs <= Judgement.GOOD.window) {
                isHit = true;
                sm.addScore(Judgement.GOOD);
            } else if (abs <= Judgement.MISS.window) {
                isHit = true;
            }
        }

        @Override
        public void onKeyReleased(double t, ScoreManager s) {
        }
    }

    static class HoldNote extends Note {
        private final double length;
        public boolean isHolding = false;
        public boolean completed = false;

        public HoldNote(int lane, double time, double length) {
            super(lane, time);
            this.length = length;
        }

        public double getLength() {
            return length;
        }

        @Override
        public void update(double currentTime, ScoreManager sm) {
            if (!isHolding && !isHit && !isMissed && currentTime > time + Judgement.MISS.window) {
                isMissed = true;
                sm.addScore(Judgement.MISS);
                SFXManager.play("failed");
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
                    SFXManager.play("failed");
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
            if (!audioFile.exists())
                throw new FileNotFoundException("Audio not found: " + path);
            AudioInputStream ais = AudioSystem.getAudioInputStream(audioFile);
            clip = AudioSystem.getClip();
            clip.open(ais);
        }

        public void play() {
            if (clip != null) {
                clip.setFramePosition(0);
                clip.start();
            }
        }

        public void stop() {
            if (clip != null)
                clip.stop();
        }

        public double getPosition() {
            return (clip == null) ? 0 : clip.getMicrosecondPosition() / 1_000_000.0;
        }

        public boolean isPlaying() {
            return clip != null && clip.isRunning();
        }

    public void close() {
        
        if (clip != null) {
            
            if (clip.isRunning()) {
                clip.stop();
            }
            
            // 2. Bersihkan buffer data yang mungkin tersisa
            clip.flush();
            
            // 3. Tutup jalur audio 
            clip.close();
            
        }
    }
    }

    static class ScoreManager {
        private int score = 0;
        private int combo = 0;

        public void addScore(Judgement j) {
            if (j == Judgement.MISS)
                combo = 0;
            else {
                combo++;
                score += j.score * (1 + (combo / 100));
            }
            System.out.println("Judgement: " + j + " | Score: " + score + " | Combo: " + combo);
        }

        public int getScore() {
            return score;
        }

        public int getCombo() {
            return combo;
        }
    }

    static class InputController implements KeyListener {
        private final GameEngine engine;
        private final Map<Integer, Integer> keyToLaneMap;
        private final boolean[] keysHeld;

        public InputController(GameEngine engine, int lanes) {
            this.engine = engine;
            this.keysHeld = new boolean[lanes];
            this.keyToLaneMap = new HashMap<>();
            int[] codes = { KeyEvent.VK_S, KeyEvent.VK_D, KeyEvent.VK_J, KeyEvent.VK_K, KeyEvent.VK_L };
            for (int i = 0; i < lanes && i < codes.length; i++)
                keyToLaneMap.put(codes[i], i);
        }

        @Override
public void keyPressed(KeyEvent e) {
    Integer lane = keyToLaneMap.get(e.getKeyCode());
    if (lane != null) {
        
        boolean isFirstPress = !keysHeld[lane];
        keysHeld[lane] = true;
        
        // Kirim status isFirstPress ke engine
        engine.handlePress(lane, isFirstPress); 
    } 
    // ...
}

        @Override
        public void keyReleased(KeyEvent e) {
            Integer lane = keyToLaneMap.get(e.getKeyCode());
            if (lane != null) {
                keysHeld[lane] = false;
                engine.handleRelease(lane);
            }
        }

        @Override
        public void keyTyped(KeyEvent e) {
        }

        public boolean isLanePressed(int lane) {
            return lane >= 0 && lane < keysHeld.length && keysHeld[lane];
        }
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
        private String currentJsonPath;
        private String bgPath;


        public void loadBeatmap(String jsonPath) throws Exception {
            this.currentJsonPath = jsonPath;

           SFXManager.loadSounds();
            Gson g = new Gson();
            BeatmapData bm = g.fromJson(new FileReader(jsonPath), BeatmapData.class);
            lanes = bm.lanes;
            approachTime = bm.approachTime;
            File jf = new File(jsonPath);
            if (bm.background != null) bgPath = new File(jf.getParent(), bm.background).getAbsolutePath();
            try { audio.load(new File(jf.getParent(), bm.audioFile).getAbsolutePath()); } catch (Exception e) {}
            notes.clear();
            for (NoteJSON n : bm.notes)
                if ("hold".equalsIgnoreCase(n.type)) notes.add(new HoldNote(n.lane, n.time, n.length));
                else notes.add(new TapNote(n.lane, n.time));
            // GANTI BAGIAN LOAD BACKGROUND DENGAN INI:
            if (bm.background != null && !bm.background.isEmpty()) {
                File bgFile = new File(jf.getParent(), bm.background);
                if (bgFile.exists()) {
                    this.backgroundPath = bgFile.getAbsolutePath();
                } else {
                    System.out.println("Warning: Background image not found at: " + bgFile.getAbsolutePath());
                    this.backgroundPath = null;
                }
            }
        }

        public void start() {
            running = true;
            audio.play();
            startTime = System.nanoTime() / 1_000_000_000.0;
        }

public void update() {
    if (!running) return;

    double currentTime = getAudioTime();

    if (!audio.isPlaying() && currentTime > 2.0) { 
        running = false; 
        
        if (audio != null) {
            audio.stop();  
            audio.close(); 
        }

        SwingUtilities.invokeLater(() -> {
            Main.showResult(scoreManager.getScore(), scoreManager.getCombo(), currentJsonPath);
        });
        
        return; 
    }

    for (Note n : notes) {
        n.update(currentTime, scoreManager);
    }
}

        public void stop() {
            if (audio != null && audio.isPlaying()) {
                audio.stop();   
                audio.close(); 
            }
        }

    public void handlePress(int lane, boolean isFirstPress) {
        if (!running) return;

        if (isFirstPress) {
            SFXManager.play("normal");
        }

        double now = getAudioTime();
        Note closest = null;
        double minDiff = Double.MAX_VALUE;
        
        for (Note n : notes) {
            if (n.lane == lane && !n.isHit && !n.isMissed) {
                double diff = n.time - now;
                if (Math.abs(diff) < minDiff) {
                    minDiff = Math.abs(diff);
                    closest = n;
                }
            }
        }

        if (closest != null && minDiff <= Judgement.MISS.window) {
            
            closest.onKeyPressed(closest.time - now, scoreManager);
        } else {
            
            if (isFirstPress) {
                scoreManager.addScore(Judgement.MISS);
                SFXManager.play("failed");
                System.out.println("Press ignored (out of judgement window). diff=" + minDiff);
            }
        }
    }

        public void handleRelease(int lane) {
            if (!running)
                return;
            for (Note n : notes)
                if (n.lane == lane && n instanceof HoldNote)
                    n.onKeyReleased(getAudioTime(), scoreManager);
        }

        public double getAudioTime() {
        // KONDISI 1: Jika Audio Sedang Jalan -> Percaya pada Audio (Paling Akurat)
        if (audio.isPlaying()) {
            return audio.getPosition(); 
        } 
        // KONDISI 2: Jika Audio Belum Jalan (Intro) -> Pakai Hitungan Manual (Bisa Minus)
        else {
            // Rumus: Waktu Sekarang - Waktu Target
            // Contoh: Sekarang detik 10. Target detik 13. Hasil = -3.0 (Intro)
            return (System.nanoTime() / 1e9) - startTime;
        }
}

        public boolean isRunning() {
            return running;
        }

        public List<Note> getNotes() {
            return notes;
        }

        public int getLanes() {
            return lanes;
        }

        public double getApproachTime() {
            return approachTime;
        }

        public int getScore() {
            return scoreManager.getScore();
        }

        public int getCombo() {
            return scoreManager.getCombo();
        }

        public String getBackgroundPath() {
            return backgroundPath;
        }
    }

    // ==========================================
    // VIEW: Rendering
    // ==========================================


        public RhythmGame() {
            setPreferredSize(new Dimension(WIDTH, HEIGHT));
            setBackground(Color.BLACK);
            setFocusable(true);
            engine = new GameEngine();
        }

        
        private void drawLanes(Graphics2D g) {
            int lanes = engine.getLanes();
            int startX = (WIDTH - (lanes * NOTE_WIDTH)) / 2;
            g.setColor(Color.WHITE);
            g.setStroke(new BasicStroke(3));
            g.drawLine(startX, HIT_Y, startX + lanes * NOTE_WIDTH, HIT_Y);

            for (int i = 0; i < lanes; i++) {
                int x = startX + i * NOTE_WIDTH;
                g.setColor(new Color(30, 30, 30, 100));
                g.fillRect(x, 0, NOTE_WIDTH, HEIGHT);
                g.setColor(Color.GRAY);
                g.drawRect(x, 0, NOTE_WIDTH, HEIGHT);
                if (input != null && input.isLanePressed(i)) {
                    g.setColor(new Color(255, 255, 255, 50));
                    g.fillRect(x, 0, NOTE_WIDTH, HEIGHT);
                    g.setColor(LANE_COLORS[i % LANE_COLORS.length]);
                    g.fillOval(x + 5, HIT_Y - 10, NOTE_WIDTH - 10, 20);
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

                if (n.isMissed())
                    continue;

                if (n instanceof TapNote && n.isHit())
                    continue;

                if (n instanceof HoldNote && ((HoldNote) n).completed)
                    continue;

                double timeDiff = n.getTime() - currentTime;

                if (!(n instanceof HoldNote) && (timeDiff > approachTime || timeDiff < -1.0))
                    continue;

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

                        if (tailLength < 0)
                            tailLength = 0;

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
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.drawString("Score: " + engine.getScore(), 20, 30);
            g.drawString("Combo: " + engine.getCombo(), 20, 60);
        }

   @Override
public void run() {

    introStartTime = System.currentTimeMillis(); 

    boolean soundIntroPlayed = false;

    while (Thread.currentThread() == gameThread) {
        
        double time = (System.currentTimeMillis() - introStartTime) / 1000.0;

        if (!soundIntroPlayed) {
            SFXManager.play("start"); 
            soundIntroPlayed = true;
        }

        if (time >= 3.0 && !engineStarted) {
            if (engine != null) engine.start();
            engineStarted = true;
        }

        if (engine != null && engineStarted) {
        engine.update();
        }
        
        repaint(); 
    }
}

}
