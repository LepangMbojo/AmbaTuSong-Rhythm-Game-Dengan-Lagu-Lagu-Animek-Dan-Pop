import java.awt.*;
import java.sql.*;
import javax.swing.*;

public class RegisterPanel extends JPanel {

    private final Main main;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private Image backgroundImage;

    public RegisterPanel(Main main) {
        this.main = main;

        
        setLayout(null);

        // === LOAD BACKGROUND ===
        backgroundImage = new ImageIcon("../beatmaps/bg.jpeg").getImage();

        JLabel title = new JLabel("REGISTER", SwingConstants.CENTER);
        title.setFont(new Font("Poppins", Font.BOLD, 28));
        title.setForeground(Color.WHITE);
        title.setBounds(0, 40, 800, 40);
        add(title);

        JLabel userLabel = new JLabel("Username:");
        userLabel.setForeground(Color.WHITE);
        userLabel.setBounds(270, 140, 260, 25);
        add(userLabel);

        usernameField = new JTextField();
        usernameField.setBounds(270, 165, 260, 32);
        add(usernameField);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setForeground(Color.WHITE);
        passLabel.setBounds(270, 210, 260, 25);
        add(passLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(270, 235, 260, 32);
        add(passwordField);

        JButton registerBtn = new JButton("Daftar");
        registerBtn.setBounds(270, 290, 260, 40);
        registerBtn.addActionListener(e -> doRegister());
        add(registerBtn);

        JButton backBtn = new JButton("Kembali");
        backBtn.setBounds(270, 340, 260, 40);
        backBtn.addActionListener(e -> main.showPanel("login"));
        add(backBtn);
    }

    // === AGAR BACKGROUND BISA DICETAK ===
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
    }

    private void doRegister() {
        String user = usernameField.getText().trim();
        String pass = new String(passwordField.getPassword());

        if (user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Semua field wajib diisi!");
            return;
        }

        try (Connection c = KoneksiDatabase.getConnection();
             PreparedStatement ps = c.prepareStatement(
                    "INSERT INTO users(username, password) VALUES(?, ?)"
             )) {

            ps.setString(1, user);
            ps.setString(2, pass);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Registrasi berhasil!");
            main.showPanel("login");

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "ERROR SQL: " + ex.getMessage());
        }
    }
}
