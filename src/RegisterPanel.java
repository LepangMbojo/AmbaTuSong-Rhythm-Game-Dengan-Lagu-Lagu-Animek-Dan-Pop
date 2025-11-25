import java.awt.*;
import java.sql.*;
import javax.swing.*;

public class RegisterPanel extends JPanel {

    private final Main main;
    private JTextField usernameField;
    private JPasswordField passwordField;

    public RegisterPanel(Main main) {
        this.main = main;
        setLayout(null);
        setBackground(Color.BLACK);

        JLabel title = new JLabel("REGISTER", SwingConstants.CENTER);
        title.setFont(new Font("Poppins", Font.BOLD, 28));
        title.setForeground(Color.BLACK);
        title.setBounds(0, 40, 800, 40);
        add(title);

        JLabel userLabel = new JLabel("Username:");
        userLabel.setBounds(270, 140, 260, 25);
        userLabel.setForeground(Color.WHITE);
        add(userLabel);

        usernameField = new JTextField();
        usernameField.setBounds(270, 165, 260, 32);
        usernameField.setForeground(Color.BLACK);
        add(usernameField);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setBounds(270, 210, 260, 25);
        passLabel.setForeground(Color.WHITE);
        add(passLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(270, 235, 260, 32);
        passLabel.setForeground(Color.BLACK);
        add(passwordField);

        JButton registerBtn = new JButton("Daftar");
        registerBtn.setBounds(270, 290, 260, 40);
        registerBtn.setForeground(Color.BLACK);
        registerBtn.addActionListener(e -> doRegister());
        add(registerBtn);

        JButton back = new JButton("Kembali");
        back.setBounds(270, 340, 260, 40);
        back.setForeground(Color.BLACK);
        back.addActionListener(e -> main.showPanel("login"));
        add(back);
    }

    private void doRegister() {
        String user = usernameField.getText().trim();
        String pass = new String(passwordField.getPassword());

        try (Connection c = KoneksiDatabase.getConnection();
                PreparedStatement ps = c.prepareStatement(
                        "INSERT INTO users(username,password) VALUES(?,?)"
                )) {

            ps.setString(1, user);
            ps.setString(2, pass);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Registrasi berhasil!");
            main.showPanel("login");

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Username sudah dipakai!");
        }
    }
}
