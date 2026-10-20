package vrms;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {
    private static final String DATABASE_NAME = "vrms";

    private static final String SERVER_URL = System.getenv().getOrDefault(
            "VRMS_DB_SERVER_URL",
            "jdbc:mysql://localhost:3306/?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
    );

    private static final String DATABASE_URL = System.getenv().getOrDefault(
            "VRMS_DB_URL",
            "jdbc:mysql://localhost:3306/" + DATABASE_NAME
                    + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
    );

    private static final String USER = System.getenv().getOrDefault("VRMS_DB_USER", "root");
    private static final String PASSWORD = System.getenv().getOrDefault("VRMS_DB_PASSWORD", "");

    private Database() {
    }

    public static Connection getConnection() throws SQLException {
        loadDriver();
        return DriverManager.getConnection(DATABASE_URL, USER, PASSWORD);
    }

    public static void initializeDatabase() throws SQLException {
        loadDriver();

        try (Connection connection = DriverManager.getConnection(SERVER_URL, USER, PASSWORD);
             Statement statement = connection.createStatement()) {
            statement.executeUpdate("CREATE DATABASE IF NOT EXISTS " + DATABASE_NAME);
        }

        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {

            statement.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS users ("
                            + "user_id INT AUTO_INCREMENT PRIMARY KEY,"
                            + "name VARCHAR(100) NOT NULL,"
                            + "email VARCHAR(100) NOT NULL UNIQUE,"
                            + "phone VARCHAR(20) NOT NULL,"
                            + "password VARCHAR(100) NOT NULL,"
                            + "role VARCHAR(20) NOT NULL"
                            + ")"
            );

            statement.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS vehicles ("
                            + "vehicle_id INT AUTO_INCREMENT PRIMARY KEY,"
                            + "owner_id INT NOT NULL,"
                            + "name VARCHAR(100) NOT NULL,"
                            + "type VARCHAR(30) NOT NULL,"
                            + "registration VARCHAR(50) NOT NULL UNIQUE,"
                            + "price_per_day DECIMAL(10,2) NOT NULL,"
                            + "availability VARCHAR(30) NOT NULL,"
                            + "approval_status VARCHAR(30) NOT NULL,"
                            + "FOREIGN KEY (owner_id) REFERENCES users(user_id)"
                            + ")"
            );

            statement.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS rentals ("
                            + "rental_id INT AUTO_INCREMENT PRIMARY KEY,"
                            + "vehicle_id INT NOT NULL,"
                            + "customer_id INT NOT NULL,"
                            + "start_date DATE NOT NULL,"
                            + "end_date DATE NOT NULL,"
                            + "rental_amount DECIMAL(10,2) NOT NULL,"
                            + "status VARCHAR(30) NOT NULL,"
                            + "FOREIGN KEY (vehicle_id) REFERENCES vehicles(vehicle_id),"
                            + "FOREIGN KEY (customer_id) REFERENCES users(user_id)"
                            + ")"
            );

            statement.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS payments ("
                            + "payment_id INT AUTO_INCREMENT PRIMARY KEY,"
                            + "rental_id INT NOT NULL UNIQUE,"
                            + "amount DECIMAL(10,2) NOT NULL,"
                            + "service_fee DECIMAL(10,2) NOT NULL,"
                            + "total_paid DECIMAL(10,2) NOT NULL,"
                            + "FOREIGN KEY (rental_id) REFERENCES rentals(rental_id)"
                            + ")"
            );
        }
    }

    private static void loadDriver() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            throw new SQLException(
                    "MySQL JDBC driver not found. Run run.bat or run.ps1 from the project root to download it automatically.",
                    ex
            );
        }
    }
}
