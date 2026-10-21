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
            System.err.println(ex.getMessage());
            System.exit(2);
        }
    }
}
