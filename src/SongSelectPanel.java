import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.*;

public class SongSelectPanel extends JPanel implements KeyListener {
    
    private Main main; 
    private List<File> beatmaps = new ArrayList<>();
    private int selectedIndex = 0;
    
    private final Font titleFont = new Font("Arial", Font.BOLD, 40);
    private final Font listFont = new Font("Arial", Font.PLAIN, 24);

    public SongSelectPanel(Main main) {
        this.main = main;
        setLayout(null);
        setBackground(Color.DARK_GRAY);
        
        // --- [FIX PENTING] ---
        // Agar panel 100% bisa menerima input keyboard
        setFocusable(true);
        setRequestFocusEnabled(true);
        
        // Tambahkan Mouse Listener: Kalau diklik, paksa ambil fokus keyboard
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println("Panel diklik -> Request Focus");
                requestFocusInWindow();
            }
        });

        addKeyListener(this);
    }

    public void refreshBeatmaps() {
        beatmaps.clear();
        File folder = new File("beatmaps"); 
        
        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles((dir, name) -> name.endsWith(".json"));
            if (files != null) {
                Collections.addAll(beatmaps, files);
            }
        } else {
            System.out.println("ERROR: Folder 'beatmaps' tidak ditemukan!");
        }
        
        if (selectedIndex >= beatmaps.size()) selectedIndex = 0;
        
        // Paksa fokus setiap kali refresh
        this.requestFocusInWindow(); 
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Background
        g2.setColor(new Color(20, 20, 30));
        g2.fillRect(0, 0, getWidth(), getHeight());

        // Judul
        g2.setColor(Color.CYAN);
        g2.setFont(titleFont);
        String title = "SELECT SONG";
        int tw = g2.getFontMetrics().stringWidth(title);
        g2.drawString(title, (getWidth() - tw) / 2, 80);

        // List Lagu
        g2.setFont(listFont);
        int startY = 180;

        if (beatmaps.isEmpty()) {
            g2.setColor(Color.RED);
            g2.drawString("No beatmaps found in 'beatmaps/'!", 100, 200);
        } else {
            for (int i = 0; i < beatmaps.size(); i++) {
                String name = beatmaps.get(i).getName().replace(".json", "");
                
                if (i == selectedIndex) {
                    // Highlight
                    g2.setColor(new Color(255, 255, 255, 40));
                    g2.fillRect(50, startY + (i * 50) - 35, getWidth() - 100, 45);
                    g2.setColor(Color.YELLOW);
                    g2.drawString("> " + name, 80, startY + (i * 50));
                } else {
                    g2.setColor(Color.GRAY);
                    g2.drawString(name, 100, startY + (i * 50));
                }
            }
        }
        
        // Footer
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.PLAIN, 14));
        g2.drawString("[ESC] Back to Menu  |  [ENTER] Play", 20, getHeight()-20);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        
        if (code == KeyEvent.VK_UP) {
            selectedIndex--;
            if (selectedIndex < 0) selectedIndex = beatmaps.size() - 1;
            repaint();
        } 
        else if (code == KeyEvent.VK_DOWN) {
            selectedIndex++;
            if (selectedIndex >= beatmaps.size()) selectedIndex = 0;
            repaint();
        } 
        else if (code == KeyEvent.VK_ESCAPE) {
            System.out.println("Tombol ESC ditekan -> Kembali ke Menu");
            if (main != null) main.showPanel("MENU"); 
        } 
        else if (code == KeyEvent.VK_ENTER) {
            // --- DEBUGGING ---
            System.out.println("Tombol ENTER ditekan!"); 
            
            if (!beatmaps.isEmpty()) {
                String path = beatmaps.get(selectedIndex).getPath();
                System.out.println("Mencoba memuat lagu: " + path);
                
                // PANGGIL MAIN
                Main.playGame(path);
            } else {
                System.out.println("Error: List lagu kosong!");
            }
        }
    }

    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}
}