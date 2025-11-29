import java.awt.*;
import java.sql.*;
import javax.sound.sampled.*;
import javax.swing.*;

public class LoginPanel extends JPanel {

    private final Main main;
    private JTextField usernameField;
    private JPasswordField passwordField;

    private Image backgroundImage;  // 🔥 Tambahan untuk background

    public LoginPanel(Main main) {
        this.main = main;
        setLayout(null);

        // === 🔥 LOAD BACKGROUND IMAGE ===
       // === FIX: load image dari folder beatmaps ===
        backgroundImage = new ImageIcon("../beatmaps/bg.jpeg").getImage();

        // (hilangkan setBackground(Color.BLACK); karena pakai gambar)
        

        JLabel title = new JLabel("LOGIN", SwingConstants.CENTER);
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

        JButton loginBtn = new JButton("Login");
        loginBtn.setBounds(270, 290, 260, 40);

        loginBtn.addActionListener(e -> {
            System.out.println("Tombol login ditekan");
            doLogin();
        });

        add(loginBtn);

        JButton registerBtn = new JButton("Register");
        registerBtn.setBounds(270, 340, 260, 40);

        registerBtn.addActionListener(e -> {
            System.out.println("Tombol register ditekan");
            main.showPanel("register");
        });

        add(registerBtn);
    }

    // === 🔥 RENDER GAMBAR BACKGROUND ===
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
    }

    // Pemutar suara
    private void playSound(String file) {
        try {
            AudioInputStream audio = AudioSystem.getAudioInputStream(getClass().getResource(file));
            Clip clip = AudioSystem.getClip();
            clip.open(audio);
            clip.start();
        } catch (Exception e) {
            System.out.println("Gagal memutar suara: " + e.getMessage());
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
             PreparedStatement ps = c.prepareStatement(
                     "SELECT * FROM users WHERE username=? AND password=?"
             )) {

            ps.setString(1, user);
            ps.setString(2, pass);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                playSound("/sounds/success.wav");
                JOptionPane.showMessageDialog(this, "Login berhasil!");
                main.onLoginSuccess(user);
            } else {
                playSound("/sounds/error.wav");
                JOptionPane.showMessageDialog(this, "Username atau password salah!");
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
