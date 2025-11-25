import java.sql.*;
import javax.swing.*;

public class KoneksiDatabase {

    private static String url, user, pass;

    public static void init(String u, String usr, String ps) {
        url = u;
        user = usr;
        pass = ps;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection c = DriverManager.getConnection(url, user, pass)) {
                System.out.println("Koneksi berhasil!");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Gagal koneksi DB: " + e.getMessage());
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, pass);
    }
}
