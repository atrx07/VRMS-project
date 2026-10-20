package vrms;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class VehicleStore {
    public static final String DELETED_BY_ADMIN = "DELETED_BY_ADMIN";

    private VehicleStore() {
    }

    public static String addVehicle(int ownerId, String name, String type,
                                    String registrationNumber, double pricePerDay) throws IOException {
        String registration = registrationNumber.trim().toUpperCase();

        if (registrationExists(registration)) {
            return "A vehicle with this registration number already exists.";
        }

        String sql = "INSERT INTO vehicles "
                + "(owner_id, name, type, registration, price_per_day, availability, approval_status) "
                + "VALUES (?, ?, ?, ?, ?, 'AVAILABLE', 'PENDING')";

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, ownerId);
            statement.setString(2, name.trim());
            statement.setString(3, type.trim());
            statement.setString(4, registration);
            statement.setDouble(5, pricePerDay);
            statement.executeUpdate();
            return null;
        } catch (SQLException ex) {
            throw databaseError(ex);
        }
    }

    public static List<String[]> getCatalogVehicles(int currentUserId) throws IOException {
        return getCatalogVehicles();
    }

    public static List<String[]> getCatalogVehicles() throws IOException {
        String sql = "SELECT * FROM vehicles "
                + "WHERE approval_status = 'APPROVED' AND availability = 'AVAILABLE' "
                + "ORDER BY vehicle_id";
        return readVehicles(sql);
    }

    public static List<String[]> getApprovedVehicles() throws IOException {
        String sql = "SELECT * FROM vehicles "
                + "WHERE approval_status = 'APPROVED' "
                + "ORDER BY vehicle_id";
        return readVehicles(sql);
    }

    public static List<String[]> getVehiclesForOwner(int ownerId) throws IOException {
        String sql = "SELECT * FROM vehicles WHERE owner_id = ? ORDER BY vehicle_id";

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, ownerId);

            try (ResultSet result = statement.executeQuery()) {
                return collectVehicles(result);
            }
        } catch (SQLException ex) {
            throw databaseError(ex);
        }
    }

    public static List<String[]> getPendingVehicles() throws IOException {
        String sql = "SELECT * FROM vehicles "
                + "WHERE approval_status = 'PENDING' "
                + "ORDER BY vehicle_id";
        return readVehicles(sql);
    }

    public static int getPendingCount() throws IOException {
        String sql = "SELECT COUNT(*) FROM vehicles WHERE approval_status = 'PENDING'";

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {
            result.next();
            return result.getInt(1);
        } catch (SQLException ex) {
            throw databaseError(ex);
        }
    }

    public static String[] getVehicleById(int vehicleId) throws IOException {
        String sql = "SELECT * FROM vehicles WHERE vehicle_id = ?";

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, vehicleId);

            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    return vehicleArray(result);
                }
            }

            return null;
        } catch (SQLException ex) {
            throw databaseError(ex);
        }
    }

    public static boolean updateApproval(int vehicleId, String approvalStatus) throws IOException {
        String sql = "UPDATE vehicles SET approval_status = ? WHERE vehicle_id = ?";

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, approvalStatus);
            statement.setInt(2, vehicleId);
            return statement.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw databaseError(ex);
        }
    }

    public static boolean updateAvailability(int vehicleId, String availability) throws IOException {
        String sql = "UPDATE vehicles SET availability = ? WHERE vehicle_id = ?";

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, availability);
            statement.setInt(2, vehicleId);
            return statement.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw databaseError(ex);
        }
    }

    public static boolean deleteVehicle(int vehicleId) throws IOException {
        String sql = "UPDATE vehicles "
                + "SET availability = 'UNAVAILABLE', approval_status = ? "
                + "WHERE vehicle_id = ? AND availability <> 'RENTED'";

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, DELETED_BY_ADMIN);
            statement.setInt(2, vehicleId);
            return statement.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw databaseError(ex);
        }
    }

    private static List<String[]> readVehicles(String sql) throws IOException {
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {
            return collectVehicles(result);
        } catch (SQLException ex) {
            throw databaseError(ex);
        }
    }

    private static List<String[]> collectVehicles(ResultSet result) throws SQLException {
        List<String[]> vehicles = new ArrayList<>();

        while (result.next()) {
            vehicles.add(vehicleArray(result));
        }

        return vehicles;
    }

    private static String[] vehicleArray(ResultSet result) throws SQLException {
        return new String[]{
                String.valueOf(result.getInt("vehicle_id")),
                String.valueOf(result.getInt("owner_id")),
                result.getString("name"),
                result.getString("type"),
                result.getString("registration"),
                String.format(Locale.US, "%.2f", result.getDouble("price_per_day")),
                result.getString("availability"),
                result.getString("approval_status")
        };
    }

    private static boolean registrationExists(String registrationNumber) throws IOException {
        String sql = "SELECT 1 FROM vehicles WHERE UPPER(registration) = UPPER(?) LIMIT 1";

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, registrationNumber);

            try (ResultSet result = statement.executeQuery()) {
                return result.next();
            }
        } catch (SQLException ex) {
            throw databaseError(ex);
        }
    }

    private static IOException databaseError(SQLException ex) {
        return new IOException("Database error: " + ex.getMessage(), ex);
    }
}
