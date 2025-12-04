import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;

public class LeaderboardPanel extends JPanel {
    
    private Main main;
    private JTable table;
    private JScrollPane scrollPane;

    public LeaderboardPanel(Main main) {
        this.main = main;
        setLayout(new BorderLayout());
        setBackground(new Color(20, 20, 30));

        // 1. JUDUL
        JLabel title = new JLabel("GLOBAL TOP SCORES", SwingConstants.CENTER);
        title.setFont(new Font("Poppins", Font.BOLD, 36));
        title.setForeground(Color.CYAN);
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(title, BorderLayout.NORTH);

        // 2. TABEL
        table = new JTable();
        table.setRowHeight(30);
        table.setFont(new Font("Arial", Font.PLAIN, 16));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 18));
        table.setBackground(new Color(40, 40, 50));
        table.setForeground(Color.WHITE);
        table.setGridColor(Color.GRAY);
        table.setEnabled(false);

        // Styling Header Tabel
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.setDefaultRenderer(Object.class, centerRenderer);

        scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(new Color(30, 30, 40));
        add(scrollPane, BorderLayout.CENTER);

        // 3. TOMBOL KEMBALI
        JButton btnBack = new JButton("Back to Menu");
        btnBack.setFont(new Font("Arial", Font.BOLD, 18));
        btnBack.setBackground(new Color(200, 50, 50));
        btnBack.setForeground(Color.WHITE);
        btnBack.setFocusPainted(false);
        btnBack.addActionListener(e -> main.showCard("MENU"));
        
        JPanel bottomPanel = new JPanel();
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        bottomPanel.add(btnBack);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    // Method untuk refresh data dari database setiap kali dibuka
    public void loadData() {
        table.setModel(KoneksiDatabase.getGlobalLeaderboard());
        
        // Atur lebar kolom (Opsional, biar rapi)
        if (table.getColumnModel().getColumnCount() > 0) {
            table.getColumnModel().getColumn(0).setPreferredWidth(50);  // Rank
            table.getColumnModel().getColumn(1).setPreferredWidth(150); // Player
            table.getColumnModel().getColumn(2).setPreferredWidth(250); // Song
            table.getColumnModel().getColumn(3).setPreferredWidth(100); // Score
            table.getColumnModel().getColumn(4).setPreferredWidth(50);  // Grade
        }
    }
}