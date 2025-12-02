import java.awt.*;
import javax.swing.*;

public class Main extends JFrame {
    
    private static Main instance; 
    private CardLayout cardLayout;
    private JPanel mainPanel;

    private LoginPanel loginPanel;       
    private RegisterPanel registerPanel; 
    private MenuPanel menuPanel;
    private SongSelectPanel songSelectPanel;
    private RhythmGame rhythmGamePanel;
    private ResultPanel resultPanel;
    private LeaderboardPanel leaderboardPanel;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::new);
    }

    public Main() {
        super("AmbaTuSong Rhythm Game");
        instance = this; 

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // --- [FIX] UKURAN TETAP 1000x600 (TIDAK FULL SCREEN) ---
        setSize(1000, 600);
        setResizable(false); // Agar tidak bisa diubah ukurannya
        setLocationRelativeTo(null); // Posisi tengah layar

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Inisialisasi Panel
        try { loginPanel = new LoginPanel(this); } catch (Exception e) {}
        try { registerPanel = new RegisterPanel(this); } catch (Exception e) {}
        try { menuPanel = new MenuPanel(this); } catch (Exception e) {}
        try { leaderboardPanel = new LeaderboardPanel(this); } catch (Exception e) {}

        
        songSelectPanel = new SongSelectPanel(this);
        rhythmGamePanel = new RhythmGame();
        resultPanel = new ResultPanel(this);

        // Add ke CardLayout
        if (loginPanel != null) mainPanel.add(loginPanel, "LOGIN");
        if (registerPanel != null) mainPanel.add(registerPanel, "REGISTER");
        if (menuPanel != null) mainPanel.add(menuPanel, "MENU");
        if (leaderboardPanel != null) mainPanel.add(leaderboardPanel, "LEADERBOARD");
        
        mainPanel.add(songSelectPanel, "SONG_SELECT");
        mainPanel.add(rhythmGamePanel, "GAME");
        mainPanel.add(resultPanel, "RESULT");
        

        add(mainPanel);
        setVisible(true);

        showCard("LOGIN"); // Langsung ke Menu
    }

    // --- METHOD NAVIGASI ---
    public static void showCard(String cardName) {
        if (instance != null) {
            if (cardName.equals("MENU") || cardName.equals("SONG_SELECT") || cardName.equals("RESULT")) {
                instance.rhythmGamePanel.stopGame(); 
            }
            
            if (cardName.equals("LEADERBOARD") && instance.leaderboardPanel != null) {
                instance.leaderboardPanel.loadData();
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
            instance.rhythmGamePanel.start(beatmapPath); // Siapkan game
            instance.cardLayout.show(instance.mainPanel, "GAME"); // Tampilkan panel game
            instance.rhythmGamePanel.requestFocusInWindow(); // Fokus keyboard
        }
    }
    
    public static void showResult(int score, int combo, String songPath) {
        if (instance != null) {
            instance.resultPanel.showResult(score, combo, songPath);
            instance.cardLayout.show(instance.mainPanel, "RESULT");
        }
    }
    
    public void showPanel(String name) { showCard(name); }
    public void goToSongSelect() { showCard("SONG_SELECT"); }
    public static void showSongSelectStatic() { showCard("SONG_SELECT"); }
    public void showLeaderboard() { showCard("LEADERBOARD"); }
    
    public void onLoginSuccess(String user) {
        JOptionPane.showMessageDialog(this, "Welcome, " + user + "!");
        showCard("MENU");
    }
}