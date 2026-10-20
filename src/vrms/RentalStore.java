package vrms;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RentalStore {
    public static final double PLATFORM_FEE_RATE = 0.10;

    private RentalStore() {
    }

    public static String createRental(int vehicleId, int customerId,
                                      LocalDate startDate, LocalDate endDate) throws IOException {
        if (endDate.isBefore(startDate)) {
            return "End date cannot be before the start date.";
        }

        try (Connection connection = Database.getConnection()) {
            connection.setAutoCommit(false);

            try {
                String vehicleSql = "SELECT owner_id, price_per_day, availability, approval_status "
                        + "FROM vehicles WHERE vehicle_id = ? FOR UPDATE";

                int ownerId;
                double pricePerDay;
                String availability;
                String approvalStatus;

                try (PreparedStatement vehicleStatement = connection.prepareStatement(vehicleSql)) {
                    vehicleStatement.setInt(1, vehicleId);

                    try (ResultSet vehicle = vehicleStatement.executeQuery()) {
                        if (!vehicle.next()) {
                            connection.rollback();
                            return "Vehicle not found.";
                        }

                        ownerId = vehicle.getInt("owner_id");
                        pricePerDay = vehicle.getDouble("price_per_day");
                        availability = vehicle.getString("availability");
                        approvalStatus = vehicle.getString("approval_status");
                    }
                }

                if (!"APPROVED".equals(approvalStatus) || !"AVAILABLE".equals(availability)) {
                    connection.rollback();
                    return "This vehicle is no longer available for rent.";
                }

                if (ownerId == customerId) {
                    connection.rollback();
                    return "You cannot rent your own vehicle.";
                }

                double rentalAmount = calculateTotal(pricePerDay, startDate, endDate);
                double platformFee = calculatePlatformFee(rentalAmount);
                double totalPaid = rentalAmount + platformFee;

                String rentalSql = "INSERT INTO rentals "
                        + "(vehicle_id, customer_id, start_date, end_date, rental_amount, status) "
                        + "VALUES (?, ?, ?, ?, ?, 'ACTIVE')";

                int rentalId;
                try (PreparedStatement rentalStatement = connection.prepareStatement(
                        rentalSql, Statement.RETURN_GENERATED_KEYS)) {
                    rentalStatement.setInt(1, vehicleId);
                    rentalStatement.setInt(2, customerId);
                    rentalStatement.setDate(3, Date.valueOf(startDate));
                    rentalStatement.setDate(4, Date.valueOf(endDate));
                    rentalStatement.setDouble(5, rentalAmount);
                    rentalStatement.executeUpdate();

                    try (ResultSet keys = rentalStatement.getGeneratedKeys()) {
                        if (!keys.next()) {
                            throw new SQLException("Could not create rental ID.");
                        }
                        rentalId = keys.getInt(1);
                    }
                }

                String paymentSql = "INSERT INTO payments "
                        + "(rental_id, amount, service_fee, total_paid) VALUES (?, ?, ?, ?)";

                try (PreparedStatement paymentStatement = connection.prepareStatement(paymentSql)) {
                    paymentStatement.setInt(1, rentalId);
                    paymentStatement.setDouble(2, rentalAmount);
                    paymentStatement.setDouble(3, platformFee);
                    paymentStatement.setDouble(4, totalPaid);
                    paymentStatement.executeUpdate();
                }

                String vehicleUpdate = "UPDATE vehicles SET availability = 'RENTED' WHERE vehicle_id = ?";
                try (PreparedStatement updateStatement = connection.prepareStatement(vehicleUpdate)) {
                    updateStatement.setInt(1, vehicleId);
                    updateStatement.executeUpdate();
                }

                connection.commit();
                return null;
            } catch (SQLException ex) {
                connection.rollback();
                throw ex;
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException ex) {
            throw databaseError(ex);
        }
    }

    public static String returnRental(int rentalId, int customerId) throws IOException {
        try (Connection connection = Database.getConnection()) {
            connection.setAutoCommit(false);

            try {
                String rentalSql = "SELECT vehicle_id, customer_id, status "
                        + "FROM rentals WHERE rental_id = ? FOR UPDATE";

                int vehicleId;
                int rentalCustomerId;
                String status;

                try (PreparedStatement rentalStatement = connection.prepareStatement(rentalSql)) {
                    rentalStatement.setInt(1, rentalId);

                    try (ResultSet rental = rentalStatement.executeQuery()) {
                        if (!rental.next()) {
                            connection.rollback();
                            return "Rental not found.";
                        }

                        vehicleId = rental.getInt("vehicle_id");
                        rentalCustomerId = rental.getInt("customer_id");
                        status = rental.getString("status");
                    }
                }

                if (rentalCustomerId != customerId) {
                    connection.rollback();
                    return "This rental does not belong to the current user.";
                }

                if (!"ACTIVE".equals(status)) {
                    connection.rollback();
                    return "This vehicle has already been returned.";
                }

                try (PreparedStatement updateRental = connection.prepareStatement(
                        "UPDATE rentals SET status = 'RETURNED' WHERE rental_id = ?")) {
                    updateRental.setInt(1, rentalId);
                    updateRental.executeUpdate();
                }

                try (PreparedStatement updateVehicle = connection.prepareStatement(
                        "UPDATE vehicles SET availability = 'AVAILABLE' WHERE vehicle_id = ?")) {
                    updateVehicle.setInt(1, vehicleId);
                    updateVehicle.executeUpdate();
                }

                connection.commit();
                return null;
            } catch (SQLException ex) {
                connection.rollback();
                throw ex;
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException ex) {
            throw databaseError(ex);
        }
    }

    public static double calculateTotal(double pricePerDay, LocalDate startDate, LocalDate endDate) {
        long days = Math.max(1, ChronoUnit.DAYS.between(startDate, endDate));
        return days * pricePerDay;
    }

    public static double calculatePlatformFee(double rentalAmount) {
        return rentalAmount * PLATFORM_FEE_RATE;
    }

    public static double calculateCustomerTotal(double rentalAmount) {
        return rentalAmount + calculatePlatformFee(rentalAmount);
    }

    public static List<String[]> getRentalsForCustomer(int customerId) throws IOException {
        String sql = rentalSelect()
                + " WHERE r.customer_id = ? ORDER BY r.rental_id DESC";

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, customerId);

            try (ResultSet result = statement.executeQuery()) {
                return collectRentals(result);
            }
        } catch (SQLException ex) {
            throw databaseError(ex);
        }
    }

    public static List<String[]> getAllRentals() throws IOException {
        String sql = rentalSelect() + " ORDER BY r.rental_id DESC";

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {
            return collectRentals(result);
        } catch (SQLException ex) {
            throw databaseError(ex);
        }
    }

    public static double getTotalPlatformEarnings() throws IOException {
        return paymentTotal("service_fee");
    }

    public static double getTotalOwnerPayouts() throws IOException {
        return paymentTotal("amount");
    }

    public static double getTotalCustomerPayments() throws IOException {
        return paymentTotal("total_paid");
    }

    private static String rentalSelect() {
        return "SELECT r.rental_id, r.vehicle_id, r.customer_id, r.start_date, r.end_date, "
                + "r.rental_amount, COALESCE(p.service_fee, 0) AS service_fee, "
                + "COALESCE(p.total_paid, r.rental_amount) AS total_paid, r.status "
                + "FROM rentals r LEFT JOIN payments p ON p.rental_id = r.rental_id";
    }

    private static List<String[]> collectRentals(ResultSet result) throws SQLException {
        List<String[]> rentals = new ArrayList<>();

        while (result.next()) {
            rentals.add(new String[]{
                    String.valueOf(result.getInt("rental_id")),
                    String.valueOf(result.getInt("vehicle_id")),
                    String.valueOf(result.getInt("customer_id")),
                    result.getDate("start_date").toLocalDate().toString(),
                    result.getDate("end_date").toLocalDate().toString(),
                    money(result.getDouble("rental_amount")),
                    money(result.getDouble("service_fee")),
                    money(result.getDouble("total_paid")),
                    result.getString("status")
            });
        }

        return rentals;
    }

    private static double paymentTotal(String column) throws IOException {
        String sql = "SELECT COALESCE(SUM(" + column + "), 0) FROM payments";

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {
            result.next();
            return result.getDouble(1);
        } catch (SQLException ex) {
            throw databaseError(ex);
        }
    }

    private static String money(double amount) {
        return String.format(Locale.US, "%.2f", amount);
    }

    private static IOException databaseError(SQLException ex) {
        return new IOException("Database error: " + ex.getMessage(), ex);
    }
}
