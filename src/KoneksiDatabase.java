import java.sql.*;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;

public class KoneksiDatabase {
    
    private static final String URL = "jdbc:mysql://localhost:3306/rhythm_db";
    private static final String USER = "root";
    private static final String PASS = "";


    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    public static void saveScore(String username, String songTitle, int score, int combo, String grade) {
        String query = "INSERT INTO scores (username, song_title, score, combo, grade) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, username);
            pstmt.setString(2, songTitle);
            pstmt.setInt(3, score);
            pstmt.setInt(4, combo);
            pstmt.setString(5, grade);
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static DefaultTableModel getGlobalLeaderboard() {
        String[] columns = {"Rank", "Player", "Song", "Score", "Grade"};
        DefaultTableModel model = new DefaultTableModel(null, columns);
        String query = "SELECT username, song_title, score, grade FROM scores ORDER BY score DESC LIMIT 10";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            int rank = 1;
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rank++);
                row.add(rs.getString("username"));
                row.add(rs.getString("song_title"));
                row.add(rs.getInt("score"));
                row.add(rs.getString("grade"));
                model.addRow(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return model;
    }
}