import java.awt.*;
import javax.swing.*;

public class Main extends JFrame {
    
    private static Main instance; 
    private CardLayout cardLayout;
    private JPanel mainPanel;

    // Panel-Panel Halaman
    private LoginPanel loginPanel;       
    private RegisterPanel registerPanel; 
    private MenuPanel menuPanel;
    private SongSelectPanel songSelectPanel;
    private RhythmGame rhythmGamePanel;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::new);
    }

    public Main() {
        super("AmbaTuSong Rhythm Game");
        instance = this; 

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 600);
        setResizable(false);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // 1. Inisialisasi Panel (Tetap load LoginPanel agar tidak error saat compile)
        try {
            loginPanel = new LoginPanel(this);       
            registerPanel = new RegisterPanel(this); 
            menuPanel = new MenuPanel(this);         
        } catch (Exception e) {
            System.out.println("Info: Panel UI error/default.");
        }
        
        songSelectPanel = new SongSelectPanel(this);
        rhythmGamePanel = new RhythmGame();

        // 2. Masukkan ke Tumpukan
        if (loginPanel != null) mainPanel.add(loginPanel, "LOGIN");
        if (registerPanel != null) mainPanel.add(registerPanel, "REGISTER");
        if (menuPanel != null) mainPanel.add(menuPanel, "MENU");
        
        mainPanel.add(songSelectPanel, "SONG_SELECT");
        mainPanel.add(rhythmGamePanel, "GAME");

        add(mainPanel);
        setVisible(true);

        // --- [BYPASS] Langsung masuk ke Pilih Lagu ---
        showCard("SONG_SELECT");
    }

    // --- METHOD NAVIGASI ---

    public static void showCard(String cardName) {
        if (instance != null) {
            if (cardName.equals("MENU") || cardName.equals("SONG_SELECT")) {
                instance.rhythmGamePanel.stopGame(); 
            }
            
            instance.cardLayout.show(instance.mainPanel, cardName);

            if (cardName.equals("MENU") && instance.menuPanel != null) instance.menuPanel.requestFocusInWindow();
            if (cardName.equals("SONG_SELECT")) {
                instance.songSelectPanel.refreshBeatmaps();
                instance.songSelectPanel.requestFocusInWindow();
            }
        }
    }

    public static void playGame(String beatmapPath) {
        if (instance != null) {
            // [FIX 2] Gunakan method .start() sesuai RhythmGame.java Anda
            instance.rhythmGamePanel.start(beatmapPath); 
            
            instance.cardLayout.show(instance.mainPanel, "GAME");
            instance.rhythmGamePanel.requestFocusInWindow();
        }
    }
    
    public void showPanel(String name) {
        showCard(name);
    }
    
    public void goToSongSelect() {
        showCard("SONG_SELECT");
    }
    
    public static void showSongSelectStatic() {
        showCard("SONG_SELECT");
    }

    // [FIX 1] Method ini dikembalikan agar LoginPanel tidak error saat dicompile
    // (Walaupun tidak dipakai karena kita bypass login)
    public void onLoginSuccess(String user) {
        JOptionPane.showMessageDialog(this, "Welcome, " + user + "!");
        showPanel("MENU");
    }
}