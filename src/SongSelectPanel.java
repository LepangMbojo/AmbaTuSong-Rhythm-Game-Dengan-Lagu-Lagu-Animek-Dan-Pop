import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.Vector;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class SongSelectPanel extends JPanel {
    
    private Main main;
    private JList<String> songList;
    private Vector<File> beatmapFiles = new Vector<>();
    private DefaultListModel<String> listModel = new DefaultListModel<>();

    public SongSelectPanel(Main main) {
        this.main = main;
        setLayout(new BorderLayout());
        setBackground(new Color(20, 20, 30));

        // 1. JUDUL DI ATAS
        JLabel title = new JLabel("SELECT SONG", SwingConstants.CENTER);
        title.setFont(new Font("Poppins", Font.BOLD, 48));
        title.setForeground(Color.CYAN);
        title.setBorder(new EmptyBorder(30, 0, 30, 0));
        add(title, BorderLayout.NORTH);

        // 2. LIST LAGU (PAKAI JLIST AGAR BISA SCROLL & KLIK)
        songList = new JList<>(listModel);
        songList.setFont(new Font("Arial", Font.PLAIN, 24));
        songList.setBackground(new Color(30, 30, 40));
        songList.setForeground(Color.WHITE);
        songList.setSelectionBackground(new Color(255, 215, 0)); // Warna Kuning saat dipilih
        songList.setSelectionForeground(Color.BLACK);
        songList.setFixedCellHeight(50);
        
        // Listener Mouse (Klik 2x untuk Main)
        songList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) { // Double Click
                    playSelectedSong();
                }
            }
        });

        // Masukkan List ke ScrollPane
        JScrollPane scrollPane = new JScrollPane(songList);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(new Color(20, 20, 30));
        add(scrollPane, BorderLayout.CENTER);

        // 3. TOMBOL NAVIGASI DI BAWAH
        JPanel bottomPanel = new JPanel();
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(new EmptyBorder(20, 0, 20, 0));

        JButton btnPlay = new JButton("PLAY");
        btnPlay.setFont(new Font("Arial", Font.BOLD, 20));
        btnPlay.setBackground(new Color(50, 200, 50));
        btnPlay.setForeground(Color.WHITE);
        btnPlay.addActionListener(e -> playSelectedSong());

        JButton btnBack = new JButton("BACK");
        btnBack.setFont(new Font("Arial", Font.BOLD, 20));
        btnBack.setBackground(new Color(200, 50, 50));
        btnBack.setForeground(Color.WHITE);
        btnBack.addActionListener(e -> main.showPanel("MENU"));

        bottomPanel.add(btnBack);
        bottomPanel.add(btnPlay);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    public void refreshBeatmaps() {
        listModel.clear();
        beatmapFiles.clear();
        
        File folder = new File("beatmaps");
        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles((dir, name) -> name.endsWith(".json"));
            if (files != null) {
                for (File f : files) {
                    beatmapFiles.add(f);
                    // Tampilkan nama file tanpa ekstensi .json
                    listModel.addElement(f.getName().replace(".json", ""));
                }
            }
        }
        
        // Pilih item pertama jika ada
        if (!listModel.isEmpty()) {
            songList.setSelectedIndex(0);
        }
        
        // Fokuskan ke list agar keyboard langsung jalan
        songList.requestFocusInWindow();
    }

    private void playSelectedSong() {
        int index = songList.getSelectedIndex();
        if (index >= 0 && index < beatmapFiles.size()) {
            String path = beatmapFiles.get(index).getPath();
            System.out.println("Memainkan: " + path);
            Main.playGame(path);
        } else {
            JOptionPane.showMessageDialog(this, "Pilih lagu dulu!");
        }
    }
}