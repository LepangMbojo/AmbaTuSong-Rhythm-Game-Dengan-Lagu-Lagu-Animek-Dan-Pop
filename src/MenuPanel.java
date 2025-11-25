import java.awt.*;
import javax.swing.*;

public class MenuPanel extends JPanel {

    private final Main main;

    public MenuPanel(Main main) {
        this.main = main;
        setLayout(null);
        setBackground(getBackground());

        JLabel title = new JLabel("MAIN MENU", SwingConstants.CENTER);
        title.setFont(new Font("Poppins", Font.BOLD, 30));
        title.setForeground(Color.WHITE);
        title.setBounds(0, 50, 800, 50);
        add(title);

        JButton play = new JButton("Mulai Game");
        play.setBounds(270, 150, 260, 45);
        play.setForeground(Color.BLACK);
        add(play);

        JButton profile = new JButton("Profil");
        profile.setBounds(270, 210, 260, 45);
        profile.setForeground(Color.BLACK);
        add(profile);

        JButton logout = new JButton("Logout");
        logout.setBounds(270, 270, 260, 45);
        logout.setForeground(Color.BLACK);
        logout.addActionListener(e -> main.showPanel("login"));
        add(logout);
    }
}
