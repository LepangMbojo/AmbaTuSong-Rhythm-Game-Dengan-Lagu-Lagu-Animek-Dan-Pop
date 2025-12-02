import java.awt.*;
import javax.swing.*;

public class MenuPanel extends JPanel {

    private final Main main;
    private Image backgroundImage;

    public MenuPanel(Main main) {
        this.main = main;
        setLayout(null);

        // === LOAD BACKGROUND IMAGE DARI FOLDER beatmaps ===
        backgroundImage = new ImageIcon("../beatmaps/bg.jpeg").getImage();

        // ===== TITLE =====
        JLabel title = new JLabel("MAIN MENU", SwingConstants.CENTER);
        title.setFont(new Font("Poppins", Font.BOLD, 30));
        title.setForeground(Color.WHITE);
        title.setBounds(0, 50, 800, 50);
        add(title);

        // ===== TOMBOL PILIH LAGU =====
        JButton selectSong = new JButton("Pilih Lagu");
        selectSong.setBounds(270, 130, 260, 45);
        selectSong.addActionListener(e -> main.goToSongSelect());
        add(selectSong);

        // ===== TOMBOL MULAI GAME =====
        JButton play = new JButton("Mulai Game");
        play.setBounds(270, 190, 260, 45);
        play.addActionListener(e -> {
            if (Session.selectedBeatmap == null) {
                JOptionPane.showMessageDialog(this, "Pilih lagu dulu!");
                return;
            }
            try {
                RhythmGame game = new RhythmGame();
                game.start(Session.selectedBeatmap);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Gagal memulai game!");
                ex.printStackTrace();
            }
        });
        add(play);

        // ===== TOMBOL PROFIL =====
        JButton profile = new JButton("Profil");
        profile.setBounds(270, 250, 260, 45);
        profile.addActionListener(e ->
                JOptionPane.showMessageDialog(this, "Username kamu: " + Session.username));
        add(profile);

        // ===== TOMBOL LEADERBOARD =====
        JButton leaderboard = new JButton("Leaderboard");   
        leaderboard.setBounds(270, 370, 260, 45);
        leaderboard.addActionListener(e -> main.goToLeaderBoard());
        add(leaderboard);

        // ===== TOMBOL LOGOUT =====
        JButton logout = new JButton("Logout");
        logout.setBounds(270, 310, 260, 45);
        logout.addActionListener(e -> {
            Session.username = null;
            main.showPanel("login");
        });
        add(logout);
    }

    // === AGAR BACKGROUND BISA DIGAMBAR ===
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
    }
}
