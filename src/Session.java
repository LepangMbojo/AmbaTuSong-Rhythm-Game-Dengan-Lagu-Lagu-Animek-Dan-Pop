public class Session {
    private static int userId = -1;
    static String username = null;

    public static void setCurrentUser(int id, String name) {
        userId = id; username = name;
    }
    public static void clear() { userId = -1; username = null; }
    public static int getUserId() { return userId; }
    public static String getUsername() { return username; }
}