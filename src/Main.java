import javax.swing.*;
import java.awt.*;

public class Main extends JFrame {

    private CardLayout card;
    private JPanel container;

    public Main() {

        // Inisialisasi koneksi database
        KoneksiDatabase.init(
                "jdbc:mysql://localhost:3306/ambatusong_db",
                "root",
                ""
        );

        setTitle("AmbaTuSong Rhythm Game");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        card = new CardLayout();
        container = new JPanel(card);

        // Daftarkan Panel
        LoginPanel login = new LoginPanel(this);
        RegisterPanel register = new RegisterPanel(this);
        MenuPanel menu = new MenuPanel(this);

        container.add(login, "login");
        container.add(register, "register");
        container.add(menu, "menu");

        add(container);

        showPanel("login");
    }

    public void showPanel(String name) {
        card.show(container, name);
    }

    // Dipanggil ketika login berhasil
    public void onLoginSuccess(String username) {
        Session.username = username;
        showPanel("menu");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Main().setVisible(true));
    }
}
