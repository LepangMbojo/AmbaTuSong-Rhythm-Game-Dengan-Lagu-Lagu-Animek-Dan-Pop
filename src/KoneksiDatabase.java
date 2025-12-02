import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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
            System.out.println("Koneksi gagal: " + e.getMessage());
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, pass);
    }
    public static List<UserScore> getLeaderboard() {
        List<UserScore> data = new ArrayList<>();
        String sql = "SELECT user.username, score.wpm, score.accuracy, score.timestamp "
                   + "FROM scores score "
                   + "JOIN users user ON score.user_id = user.user_id "
                   + "ORDER BY score.wpm DESC "
                   + "LIMIT 10";

        try (Connection conn = getConnection();
            Statement statement = conn.createStatement();
            ResultSet hasilQuery = statement.executeQuery(sql)) {
            while (hasilQuery.next()) {
                data.add(new UserScore(
                    hasilQuery.getString("username"),
                    hasilQuery.getDouble("wpm"),
                    hasilQuery.getDouble("accuracy"),
                    hasilQuery.getTimestamp("timestamp")
                ));
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }
}
