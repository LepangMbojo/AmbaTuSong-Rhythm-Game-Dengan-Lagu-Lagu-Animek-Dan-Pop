import java.awt.*;
import javax.swing.*;

public class Main extends JFrame {

    private CardLayout card;
    private JPanel container;

    public Main() {

        // Inisialisasi koneksi database
        KoneksiDatabase.init(
                "jdbc:mysql://localhost:3306/ambatusong",
                "root",
                ""
        );

        setTitle("AmbaTuSong Rhythm Game");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Gunakan CardLayout
        card = new CardLayout();
        container = new JPanel(card);

        // =============================
        // REGISTER PANEL YANG TIDAK BERAT
        // =============================
        container.add(new LoginPanel(this), "login");
        container.add(new RegisterPanel(this), "register");
        container.add(new MenuPanel(this), "menu");

        add(container);

        SwingUtilities.invokeLater(() -> showPanel("login"));
    }

    // Fungsi ganti panel
    public void showPanel(String name) {
        card.show(container, name);
    }

    // Fungsi pergi ke song select
    public void goToSongSelect() {
        SongSelectPanel songPanel = new SongSelectPanel(this);
        container.add(songPanel, "songselect");
        showPanel("songselect");
    }


    // Dipanggil ketika login sukses
    public void onLoginSuccess(String username) {
        Session.username = username;
        Session.selectedBeatmap = null;
        showPanel("menu");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Main main = new Main();
            main.setVisible(true);
        });
    }
}
