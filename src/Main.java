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
        
        // UKURAN TETAP 1000x600 
        setSize(1000, 600);
        setResizable(false); // Agar tidak bisa diubah ukurannya
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Inisialisasi Panel
        loginPanel = new LoginPanel(this); 
        registerPanel = new RegisterPanel(this);
        menuPanel = new MenuPanel(this); 
        leaderboardPanel = new LeaderboardPanel(this); 

        
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

        showCard("LOGIN"); 
    }

    //METHOD NAVIGASI
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
            instance.rhythmGamePanel.start(beatmapPath); 
            instance.cardLayout.show(instance.mainPanel, "GAME"); 
            instance.rhythmGamePanel.requestFocusInWindow(); 
        }
    }
    
    public static void showResult(int score, int combo, String songPath) {
        if (instance != null) {
            instance.resultPanel.showResult(score, combo, songPath);
            instance.cardLayout.show(instance.mainPanel, "RESULT");
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

    public void showLeaderboard() { 
        showCard("LEADERBOARD"); 
    }
    
    public void onLoginSuccess(String user) {
        showCard("MENU");
    }
}