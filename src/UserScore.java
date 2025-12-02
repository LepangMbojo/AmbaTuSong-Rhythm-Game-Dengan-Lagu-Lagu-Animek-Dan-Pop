import java.sql.Timestamp;
public class UserScore {
    private String username;
    private double wpm;
    private double accuracy;
    private Timestamp timestamp;

    public UserScore(String username, double wpm, double accuracy, Timestamp timestamp) {
        this.username = username;
        this.wpm = wpm;
        this.accuracy = accuracy;
        this.timestamp = timestamp;
    }

    public String getUsername() {
        return username;
    }

    public double getWpm() {
        return wpm;
    }

    public double getAccuracy() {
        return accuracy;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }
}
