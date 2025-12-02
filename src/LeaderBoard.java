import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

public class LeaderBoard extends JPanel {

    private Main mainApp;
    private JTable tabelLeaderboard;
    private DefaultTableModel modelTabel;
    private JButton backBtn;

    public LeaderBoard(Main mainApp) {
        this.mainApp = mainApp;
        setLayout(new BorderLayout(10, 10));
        setBackground(UIHelper.BG_COLOR);

        JLabel title = new JLabel("Top 10 Leaderboard", SwingConstants.CENTER);
        title.setFont(UIHelper.STATS_FONT.deriveFont(Font.BOLD, 26));
        title.setForeground(UIHelper.HIGHLIGHT_COLOR);
        title.setBorder(BorderFactory.createEmptyBorder(30, 10, 30, 10));
        add(title, BorderLayout.NORTH);

        String[] columnNames = {"Username", "WPM", "Akurasi (%)", "Waktu"};
        modelTabel = new DefaultTableModel(columnNames, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        tabelLeaderboard = new JTable(modelTabel);
        tabelLeaderboard.setFont(UIHelper.SANS_SERIF_FONT.deriveFont(Font.PLAIN, 14));
        tabelLeaderboard.setForeground(UIHelper.TEXT_COLOR);
        tabelLeaderboard.setBackground(UIHelper.DARK_FIELD_COLOR);
        
        tabelLeaderboard.setSelectionBackground(UIHelper.HIGHLIGHT_COLOR);
        tabelLeaderboard.setSelectionForeground(Color.BLACK);

        tabelLeaderboard.setGridColor(new Color(70, 70, 70));
        tabelLeaderboard.setRowHeight(28);

        tabelLeaderboard.getTableHeader().setFont(UIHelper.SANS_SERIF_FONT.deriveFont(Font.BOLD, 16));
        tabelLeaderboard.getTableHeader().setBackground(UIHelper.HIGHLIGHT_COLOR);
        tabelLeaderboard.getTableHeader().setForeground(Color.BLACK);
        tabelLeaderboard.getTableHeader().setReorderingAllowed(false);

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < tabelLeaderboard.getColumnCount(); i++) {
            tabelLeaderboard.getColumnModel().getColumn(i).setCellRenderer(center);
        }

        JScrollPane scrollPane = new JScrollPane(tabelLeaderboard);
        scrollPane.getViewport().setBackground(UIHelper.DARK_FIELD_COLOR);
        scrollPane.setBackground(UIHelper.DARK_FIELD_COLOR);
        scrollPane.setBorder(BorderFactory.createLineBorder(UIHelper.HIGHLIGHT_COLOR, 2));
        add(scrollPane, BorderLayout.CENTER);

        backBtn = UIHelper.createButton("Kembali ke Menu", true);
        backBtn.addActionListener(e -> mainApp.onLoginSuccess(Session.username));

        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(UIHelper.BG_COLOR);
        bottomPanel.add(backBtn);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    public void printDataLeaderBoard() {
        modelTabel.setRowCount(0);
        List<UserScore> leaderboardData = KoneksiDatabase.getLeaderboard();

        for (UserScore row : leaderboardData) {
            modelTabel.addRow(new Object[] {
                row.getUsername(),
                String.format("%.2f", row.getWpm()),
                String.format("%.2f", row.getAccuracy()),
                row.getTimestamp().toString()
            });
        }
    }
}
