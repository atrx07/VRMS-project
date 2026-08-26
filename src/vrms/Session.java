package vrms;

public class Session {
    public static int userId = -1;
    public static String name = "";
    public static String email = "";
    public static String role = "";

    private Session() {
    }

    public static void start(int id, String userName, String userEmail, String userRole) {
        userId = id;
        name = userName;
        email = userEmail;
        role = userRole;
    }

    public static void clear() {
        userId = -1;
        name = "";
        email = "";
        role = "";
    }
}
