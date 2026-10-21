package vrms;

import java.sql.SQLException;

public class DatabaseCheck {
    private DatabaseCheck() {
    }

    public static void main(String[] args) {
        try {
            Database.initializeDatabase();
            System.out.println("MySQL connection successful.");
        } catch (SQLException ex) {
            // Print expected connection/authentication failures to standard output.
            // The launcher reads the exit code to decide whether login succeeded.
            System.out.println(ex.getMessage());
            System.exit(2);
        }
    }
}
