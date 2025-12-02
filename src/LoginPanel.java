import java.awt.*;
import java.sql.*;
import javax.swing.*;

public class LoginPanel extends JPanel {

    private final Main main;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private Image backgroundImage;

    public LoginPanel(Main main) {
        this.main = main;
        setLayout(new GridBagLayout()); // Gunakan GridBag agar center otomatis
        GridBagConstraints gbc = new GridBagConstraints();

        // === LOAD BACKGROUND ===
        try { backgroundImage = new ImageIcon("beatmaps/bg.jpeg").getImage(); } catch (Exception e) {}

        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // TITLE
        JLabel title = new JLabel("LOGIN", SwingConstants.CENTER);
        title.setFont(new Font("Poppins", Font.BOLD, 40));
        title.setForeground(Color.CYAN);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        add(title, gbc);

        // USERNAME
        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(new Font("Arial", Font.BOLD, 16));
        userLabel.setForeground(Color.WHITE);
        gbc.gridy = 1; gbc.gridwidth = 1;
        add(userLabel, gbc);

        usernameField = new JTextField(15);
        usernameField.setFont(new Font("Arial", Font.PLAIN, 16));
        gbc.gridx = 1;
        add(usernameField, gbc);

        // PASSWORD
        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("Arial", Font.BOLD, 16));
        passLabel.setForeground(Color.WHITE);
        gbc.gridx = 0; gbc.gridy = 2;
        add(passLabel, gbc);

        passwordField = new JPasswordField(15);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 16));
        gbc.gridx = 1;
        add(passwordField, gbc);

        // BUTTONS PANEL
        JPanel btnPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        btnPanel.setOpaque(false);

        JButton loginBtn = new JButton("Login");
        styleButton(loginBtn, new Color(50, 200, 50));
        loginBtn.addActionListener(e -> doLogin());

        JButton registerBtn = new JButton("Register");
        styleButton(registerBtn, new Color(50, 100, 200));
        registerBtn.addActionListener(e -> main.showPanel("REGISTER"));

        btnPanel.add(loginBtn);
        btnPanel.add(registerBtn);

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
            g.setColor(new Color(0, 0, 0, 150)); // Gelapkan background
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    private void doLogin() {
        String user = usernameField.getText().trim();
        String pass = new String(passwordField.getPassword());

        if (user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username / Password tidak boleh kosong!");
            return;
        }

        try (Connection c = KoneksiDatabase.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM users WHERE username=? AND password=?")) {

            ps.setString(1, user);
            ps.setString(2, pass);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Session.username = user; // Simpan session
                main.onLoginSuccess(user);
            } else {
                JOptionPane.showMessageDialog(this, "Username atau password salah!");
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage());
        }
    }
}