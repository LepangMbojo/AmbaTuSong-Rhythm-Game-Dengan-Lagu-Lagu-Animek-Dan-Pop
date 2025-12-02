import java.awt.*;
import javax.swing.*;

public class ResultPanel extends JPanel {
    
    private Main main;
    private JLabel lblTitle, lblUser, lblScore, lblCombo, lblGrade;
    private JButton btnRetry, btnMenu;

    public ResultPanel(Main main) {
        this.main = main;
        setLayout(new GridBagLayout());
        setBackground(new Color(20, 20, 30)); // Warna gelap

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;

        // --- TITLE ---
        lblTitle = new JLabel("SONG CLEARED!", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 40));
        lblTitle.setForeground(Color.CYAN);
        gbc.gridy = 0;
        add(lblTitle, gbc);

        // --- GRADE (S/A/B/C/D) ---
        lblGrade = new JLabel("S", SwingConstants.CENTER);
        lblGrade.setFont(new Font("Serif", Font.BOLD, 100));
        lblGrade.setForeground(Color.YELLOW);
        gbc.gridy = 1;
        add(lblGrade, gbc);

        // --- USERNAME ---
        lblUser = new JLabel("Player: -", SwingConstants.CENTER);
        lblUser.setFont(new Font("Arial", Font.PLAIN, 20));
        lblUser.setForeground(Color.WHITE);
        gbc.gridy = 2;
        add(lblUser, gbc);

        // --- SCORE ---
        lblScore = new JLabel("Score: 0", SwingConstants.CENTER);
        lblScore.setFont(new Font("Arial", Font.BOLD, 30));
        lblScore.setForeground(Color.WHITE);
        gbc.gridy = 3;
        add(lblScore, gbc);

        // --- COMBO ---
        lblCombo = new JLabel("Max Combo: 0", SwingConstants.CENTER);
        lblCombo.setFont(new Font("Arial", Font.PLAIN, 20));
        lblCombo.setForeground(Color.LIGHT_GRAY);
        gbc.gridy = 4;
        add(lblCombo, gbc);

        // --- BUTTONS ---
        JPanel btnPanel = new JPanel();
        btnPanel.setOpaque(false);
        
        btnRetry = new JButton("Retry");
        btnMenu = new JButton("Back to Menu");
        
        styleButton(btnRetry);
        styleButton(btnMenu);

        btnRetry.addActionListener(e -> main.playGame(Session.currentSongTitle)); // Simplifikasi path
        btnMenu.addActionListener(e -> main.showCard("SONG_SELECT"));

        btnPanel.add(btnRetry);
        btnPanel.add(btnMenu);
        
        gbc.gridy = 5;
        gbc.insets = new Insets(30, 10, 10, 10);
        add(btnPanel, gbc);
    }

    private void styleButton(JButton btn) {
        btn.setFont(new Font("Arial", Font.BOLD, 16));
        btn.setBackground(Color.DARK_GRAY);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
    }

    // Method ini dipanggil saat game selesai
    public void showResult(int score, int combo, String songPath) {
        Session.currentSongTitle = songPath; // Simpan path buat retry
        
        lblUser.setText("Player: " + Session.username);
        lblScore.setText("Score: " + score);
        lblCombo.setText("Max Combo: " + combo);
        
        // Tentukan Grade
        String grade;
        if (score >= 500000) grade = "S";
        else if (score >= 30000) grade = "A";
        else if (score >= 20000) grade = "B";
        else grade = "C";
        
        lblGrade.setText(grade);
        
        // Simpan ke Database
        // (Mengambil nama file saja dari path)
        String songName = new java.io.File(songPath).getName().replace(".json", "");
        KoneksiDatabase.saveScore(Session.username, songName, score, combo, grade);
    }
}