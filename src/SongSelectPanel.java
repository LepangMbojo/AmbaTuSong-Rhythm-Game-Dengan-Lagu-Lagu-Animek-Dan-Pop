import java.awt.*;
import java.io.File;
import javax.swing.*;

public class SongSelectPanel extends JPanel {

    private final Main main;
    private File[] songFiles;

    public SongSelectPanel(Main main) {
        this.main = main;

        setLayout(null);
        setBackground(Color.BLACK);

        JLabel title = new JLabel("PILIH LAGU", SwingConstants.CENTER);
        title.setFont(new Font("Poppins", Font.BOLD, 26));
        title.setForeground(Color.WHITE);
        title.setBounds(0, 20, 800, 40);
        add(title);

        // ===== DAPATKAN FOLDER beatmaps =====
        // Pastikan path mengarah ke root project
        File dir = new File(System.getProperty("user.dir") + File.separator + "../beatmaps");
        dir = dir.getAbsoluteFile(); // pastikan absolute
        System.out.println("Cek folder beatmaps: " + dir.getAbsolutePath());

        if (!dir.exists()) {
            JOptionPane.showMessageDialog(this, "Folder beatmaps tidak ditemukan!");
            songFiles = new File[0];
        } else {
            File[] files = dir.listFiles((d, f) -> f.toLowerCase().endsWith(".json"));
            if (files == null || files.length == 0) {
                JOptionPane.showMessageDialog(this, "Tidak ada lagu di folder beatmaps!");
                files = new File[0];
            }
            songFiles = files;
        }

        // ===== TAMBAHKAN KE LISTMODEL =====
        DefaultListModel<String> listModel = new DefaultListModel<>();
        for (File f : songFiles) {
            System.out.println("Ditemukan lagu: " + f.getName());
            listModel.addElement(f.getName());
        }

        JList<String> songList = new JList<>(listModel);
        songList.setBounds(200, 80, 400, 300);
        add(songList);

        JButton pilih = new JButton("Pilih Lagu");
        pilih.setBounds(200, 400, 190, 40);
        pilih.addActionListener(e -> {
            String selected = songList.getSelectedValue();
            if (selected == null) {
                JOptionPane.showMessageDialog(this, "Pilih lagu dulu!");
                return;
            }

            // Gunakan absolute path untuk menghindari error
            for (File f : songFiles) {
                if (f.getName().equals(selected)) {
                    Session.selectedBeatmap = f.getAbsolutePath();
                    break;
                }
            }
            JOptionPane.showMessageDialog(this, "Lagu dipilih: " + selected);
            main.showPanel("menu");
        });
        add(pilih);

        JButton back = new JButton("Kembali");
        back.setBounds(410, 400, 190, 40);
        back.addActionListener(e -> main.showPanel("menu"));
        add(back);
    }
}
