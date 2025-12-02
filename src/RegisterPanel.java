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
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        try { backgroundImage = new ImageIcon("beatmaps/bg.jpeg").getImage(); } catch (Exception e) {}

        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // TITLE
        JLabel title = new JLabel("REGISTER", SwingConstants.CENTER);
        title.setFont(new Font("Poppins", Font.BOLD, 40));
        title.setForeground(Color.ORANGE);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        add(title, gbc);

        // INPUTS
        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(new Font("Arial", Font.BOLD, 16));
        userLabel.setForeground(Color.WHITE);
        gbc.gridy = 1; gbc.gridwidth = 1;
        add(userLabel, gbc);

        usernameField = new JTextField(15);
        usernameField.setFont(new Font("Arial", Font.PLAIN, 16));
        gbc.gridx = 1;
        add(usernameField, gbc);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("Arial", Font.BOLD, 16));
        passLabel.setForeground(Color.WHITE);
        gbc.gridx = 0; gbc.gridy = 2;
        add(passLabel, gbc);

        passwordField = new JPasswordField(15);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 16));
        gbc.gridx = 1;
        add(passwordField, gbc);

        // BUTTONS
        JPanel btnPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        btnPanel.setOpaque(false);

        JButton registerBtn = new JButton("Daftar");
        styleButton(registerBtn, new Color(50, 100, 200));
        registerBtn.addActionListener(e -> doRegister());

        JButton backBtn = new JButton("Kembali");
        styleButton(backBtn, new Color(200, 50, 50));
        backBtn.addActionListener(e -> main.showPanel("LOGIN"));

        btnPanel.add(registerBtn);
        btnPanel.add(backBtn);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2; gbc.insets = new Insets(30, 10, 10, 10);
        add(btnPanel, gbc);
    }

    private void styleButton(JButton btn, Color bg) {
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(100, 40));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            g.setColor(new Color(0, 0, 0, 150));
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    private void doRegister() {
        String user = usernameField.getText().trim();
        String pass = new String(passwordField.getPassword());

        if (user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Semua field wajib diisi!");
            return;
        }

        try (Connection c = KoneksiDatabase.getConnection();
             PreparedStatement ps = c.prepareStatement("INSERT INTO users(username, password) VALUES(?, ?)")) {

            ps.setString(1, user);
            ps.setString(2, pass);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Registrasi berhasil! Silakan Login.");
            main.showPanel("LOGIN");

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal Daftar: " + ex.getMessage());
        }
    }
}