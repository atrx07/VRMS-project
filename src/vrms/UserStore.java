package vrms;

import javax.swing.JOptionPane;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserStore {
    public static final String DEFAULT_ADMIN_EMAIL = "admin@vrms.com";
    public static final String DEFAULT_ADMIN_PASSWORD = "admin123";

    private UserStore() {
    }

    public static boolean ensureDefaultAdmin() {
        try {
            Database.initializeDatabase();

            String sql = "INSERT IGNORE INTO users (name, email, phone, password, role) "
                    + "VALUES (?, ?, ?, ?, ?)";

            try (Connection connection = Database.getConnection();
                 PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, "Administrator");
                statement.setString(2, DEFAULT_ADMIN_EMAIL);
                statement.setString(3, "0000000000");
                statement.setString(4, DEFAULT_ADMIN_PASSWORD);
                statement.setString(5, "ADMIN");
                statement.executeUpdate();
            }

            return true;
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    "VRMS could not connect to MySQL.\n" + ex.getMessage(),
                    "Startup Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public static String registerCustomer(String name, String email,
                                          String phone, String password) throws IOException {
        name = name.trim();
        email = email.trim();
        phone = phone.trim();

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty()) {
            return "Please fill in all fields.";
        }

        if (emailExists(email)) {
            return "An account with this email already exists.";
        }

        String sql = "INSERT INTO users (name, email, phone, password, role) VALUES (?, ?, ?, ?, 'CUSTOMER')";

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, name);
            statement.setString(2, email);
            statement.setString(3, phone);
            statement.setString(4, password);
            statement.executeUpdate();
            return null;
        } catch (SQLException ex) {
            throw databaseError(ex);
        }
    }

    public static boolean login(String email, String password, String requiredRole) throws IOException {
        String sql = "SELECT user_id, name, email, role FROM users "
                + "WHERE LOWER(email) = LOWER(?) AND password = ? AND role = ?";

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email.trim());
            statement.setString(2, password);
            statement.setString(3, requiredRole.toUpperCase());

            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    Session.start(
                            result.getInt("user_id"),
                            result.getString("name"),
                            result.getString("email"),
                            result.getString("role")
                    );
                    return true;
                }
            }

            return false;
        } catch (SQLException ex) {
            throw databaseError(ex);
        }
    }

    public static boolean emailExists(String email) throws IOException {
        String sql = "SELECT 1 FROM users WHERE LOWER(email) = LOWER(?) LIMIT 1";

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email.trim());

            try (ResultSet result = statement.executeQuery()) {
                return result.next();
            }
        } catch (SQLException ex) {
            throw databaseError(ex);
        }
    }

    public static String findNameById(int userId) throws IOException {
        String[] user = getUserById(userId);
        return user == null ? "User " + userId : user[1];
    }

    public static String[] getUserById(int userId) throws IOException {
        String sql = "SELECT user_id, name, email, phone, password, role FROM users WHERE user_id = ?";

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);

            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    return userArray(result);
                }
            }

            return null;
        } catch (SQLException ex) {
            throw databaseError(ex);
        }
    }

    public static String updateProfile(int userId, String name, String email,
                                       String phone, String newPassword) throws IOException {
        name = name.trim();
        email = email.trim();
        phone = phone.trim();

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty()) {
            return "Name, email and phone cannot be empty.";
        }

        String duplicateSql = "SELECT 1 FROM users WHERE LOWER(email) = LOWER(?) AND user_id <> ? LIMIT 1";

        try (Connection connection = Database.getConnection();
             PreparedStatement duplicate = connection.prepareStatement(duplicateSql)) {

            duplicate.setString(1, email);
            duplicate.setInt(2, userId);

            try (ResultSet result = duplicate.executeQuery()) {
                if (result.next()) {
                    return "Another account already uses this email.";
                }
            }

            String updateSql;
            if (newPassword == null || newPassword.isEmpty()) {
                updateSql = "UPDATE users SET name = ?, email = ?, phone = ? WHERE user_id = ?";
            } else {
                updateSql = "UPDATE users SET name = ?, email = ?, phone = ?, password = ? WHERE user_id = ?";
            }

            try (PreparedStatement update = connection.prepareStatement(updateSql)) {
                update.setString(1, name);
                update.setString(2, email);
                update.setString(3, phone);

                if (newPassword == null || newPassword.isEmpty()) {
                    update.setInt(4, userId);
                } else {
                    update.setString(4, newPassword);
                    update.setInt(5, userId);
                }

                if (update.executeUpdate() == 0) {
                    return "Account not found.";
                }
            }

            String[] updatedUser = getUserById(userId);
            if (updatedUser != null) {
                Session.start(userId, updatedUser[1], updatedUser[2], updatedUser[5]);
            }

            return null;
        } catch (SQLException ex) {
            throw databaseError(ex);
        }
    }

    private static String[] userArray(ResultSet result) throws SQLException {
        return new String[]{
                String.valueOf(result.getInt("user_id")),
                result.getString("name"),
                result.getString("email"),
                result.getString("phone"),
                result.getString("password"),
                result.getString("role")
        };
    }

    private static IOException databaseError(SQLException ex) {
        return new IOException("Database error: " + ex.getMessage(), ex);
    }
}
