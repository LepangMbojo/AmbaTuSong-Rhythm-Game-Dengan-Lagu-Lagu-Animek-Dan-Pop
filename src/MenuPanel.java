import java.awt.*;
import javax.swing.*;

public class MenuPanel extends JPanel {

    private final Main main;
    private Image backgroundImage;

    public MenuPanel(Main main) {
        this.main = main;
        setLayout(null); 

        // Load Background
            backgroundImage = new ImageIcon("beatmaps/MainAmba.png").getImage(); 

        // Posisi Tengah Horizontal: (1000 - Lebar) / 2
        // Lebar Tombol: 300, Tinggi: 50
        int btnX = (1000 - 300) / 2; 

        // ===== TITLE =====
        JLabel title = new JLabel("MAIN MENU", SwingConstants.CENTER);
        title.setFont(new Font("Poppins", Font.BOLD, 48));
        title.setForeground(Color.WHITE);
        title.setBounds(0, 50, 1000, 60); // Lebar 1000 agar center align jalan
        add(title);

        // ===== TOMBOL PILIH LAGU =====
        JButton selectSong = new JButton("Pilih Lagu");
        selectSong.setBounds(btnX, 150, 300, 50);
        selectSong.setFont(new Font("Arial", Font.BOLD, 18));
        selectSong.addActionListener(e -> main.goToSongSelect());
        add(selectSong);

        // ===== TOMBOL LEADERBOARD =====
        JButton btnLeaderboard = new JButton("Leaderboard");
        btnLeaderboard.setBounds(btnX, 220, 300, 50);
        btnLeaderboard.setFont(new Font("Arial", Font.BOLD, 18));
        btnLeaderboard.setBackground(new Color(255, 215, 0));
        btnLeaderboard.setForeground(Color.BLACK);
        btnLeaderboard.addActionListener(e -> main.showLeaderboard());
        add(btnLeaderboard);

        // ===== TOMBOL LOGOUT =====
        JButton logout = new JButton("Logout");
        logout.setBounds(btnX, 290, 300, 50);
        logout.setFont(new Font("Arial", Font.BOLD, 18));
        logout.setBackground(new Color(255, 100, 100));
        logout.addActionListener(e -> {
            Session.username = null;
            main.showPanel("LOGIN"); 
        });
        add(logout);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            g.setColor(new Color(0, 0, 0, 100));
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }
}