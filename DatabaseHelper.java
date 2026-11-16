package data;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

public class DatabaseHelper {
    private static String dbHost = "localhost";
    private static String dbPort = "3306";
    private static String dbName = "supermarket_db";
    private static String dbUser = "root";
    private static String dbPass = "root"; // Default fallback

    private static boolean driverLoaded = false;
    private static boolean dbAvailable = false;

    static {
        loadConfig();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            driverLoaded = true;
        } catch (ClassNotFoundException e) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                driverLoaded = true;
            } catch (ClassNotFoundException ex) {
                driverLoaded = false;
                System.err.println("[DATABASE] MySQL Driver not found in classpath.");
            }
        }

        if (driverLoaded) {
            initializeDatabase();
        }
    }

    private static void loadConfig() {
        Properties props = new Properties();
        File propFile = new File("db.properties");
        if (propFile.exists()) {
            try (InputStream in = new FileInputStream(propFile)) {
                props.load(in);
                dbHost = props.getProperty("db.host", dbHost);
                dbPort = props.getProperty("db.port", dbPort);
                dbName = props.getProperty("db.name", dbName);
                dbUser = props.getProperty("db.user", dbUser);
                dbPass = props.getProperty("db.password", dbPass);
            } catch (Exception ignored) {}
        }
    }

    private static String getBaseUrl() {
        return "jdbc:mysql://" + dbHost + ":" + dbPort + "/?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    }

    private static String getDbUrl() {
        return "jdbc:mysql://" + dbHost + ":" + dbPort + "/" + dbName + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    }

    public static boolean isDriverLoaded() {
        return driverLoaded;
    }

    public static boolean isDatabaseAvailable() {
        return dbAvailable;
    }

    public static Connection getConnection() throws SQLException {
        if (!driverLoaded) {
            throw new SQLException("MySQL JDBC Driver not loaded.");
        }
        return DriverManager.getConnection(getDbUrl(), dbUser, dbPass);
    }

    public static synchronized void initializeDatabase() {
        loadConfig();

        System.out.println("[DATABASE] Attempting connection to MySQL at " + dbHost + ":" + dbPort + " with user '" + dbUser + "'...");

        try (Connection conn = DriverManager.getConnection(getBaseUrl(), dbUser, dbPass);
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS `" + dbName + "`");
            dbAvailable = true;
            System.out.println("[DATABASE] Connected successfully! Verified database '" + dbName + "'.");

        } catch (SQLException e) {
            System.err.println("[DATABASE ERROR] Could not connect to MySQL: " + e.getMessage());
            System.err.println("[DATABASE HINT] Make sure MySQL service is running and verify 'db.password' in db.properties!");
            dbAvailable = false;
            return;
        }

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS `users` (" +
                    "`id` INT AUTO_INCREMENT PRIMARY KEY, " +
                    "`username` VARCHAR(50) NOT NULL UNIQUE, " +
                    "`password` VARCHAR(100) NOT NULL, " +
                    "`role` VARCHAR(30) NOT NULL DEFAULT 'Cashier', " +
                    "`full_name` VARCHAR(100) NOT NULL, " +
                    "`created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");

            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS `products` (" +
                    "`id` VARCHAR(50) PRIMARY KEY, " +
                    "`name` VARCHAR(150) NOT NULL, " +
                    "`category` VARCHAR(50) NOT NULL, " +
                    "`cost_price` DECIMAL(10, 2) NOT NULL, " +
                    "`selling_price` DECIMAL(10, 2) NOT NULL, " +
                    "`stock_quantity` INT NOT NULL DEFAULT 0, " +
                    "`min_stock_threshold` INT NOT NULL DEFAULT 10, " +
                    "`unit` VARCHAR(20) NOT NULL DEFAULT 'pcs', " +
                    "`updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP)");

            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS `customers` (" +
                    "`id` VARCHAR(50) PRIMARY KEY, " +
                    "`name` VARCHAR(100) NOT NULL, " +
                    "`phone` VARCHAR(20) NOT NULL UNIQUE, " +
                    "`email` VARCHAR(100), " +
                    "`loyalty_points` INT NOT NULL DEFAULT 0, " +
                    "`total_spent` DECIMAL(12, 2) NOT NULL DEFAULT 0.00, " +
                    "`tier` VARCHAR(30) NOT NULL DEFAULT 'Bronze', " +
                    "`created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");

            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS `invoices` (" +
                    "`invoice_number` VARCHAR(50) PRIMARY KEY, " +
                    "`date_time` DATETIME NOT NULL, " +
                    "`customer_id` VARCHAR(50), " +
                    "`cashier_name` VARCHAR(100) NOT NULL, " +
                    "`sub_total` DECIMAL(12, 2) NOT NULL, " +
                    "`tax_rate_percent` DECIMAL(5, 2) NOT NULL DEFAULT 5.00, " +
                    "`tax_amount` DECIMAL(12, 2) NOT NULL, " +
                    "`discount_amount` DECIMAL(12, 2) NOT NULL DEFAULT 0.00, " +
                    "`grand_total` DECIMAL(12, 2) NOT NULL, " +
                    "`payment_method` VARCHAR(50) NOT NULL DEFAULT 'Cash', " +
                    "`amount_paid` DECIMAL(12, 2) NOT NULL, " +
                    "`change_due` DECIMAL(12, 2) NOT NULL DEFAULT 0.00, " +
                    "`points_earned` INT NOT NULL DEFAULT 0, " +
                    "`points_redeemed` INT NOT NULL DEFAULT 0, " +
                    "FOREIGN KEY (`customer_id`) REFERENCES `customers`(`id`) ON DELETE SET NULL)");

            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS `invoice_items` (" +
                    "`id` INT AUTO_INCREMENT PRIMARY KEY, " +
                    "`invoice_number` VARCHAR(50) NOT NULL, " +
                    "`product_id` VARCHAR(50) NOT NULL, " +
                    "`quantity` INT NOT NULL, " +
                    "`unit_price` DECIMAL(10, 2) NOT NULL, " +
                    "`discount_percent` DECIMAL(5, 2) NOT NULL DEFAULT 0.00, " +
                    "`net_total` DECIMAL(12, 2) NOT NULL, " +
                    "FOREIGN KEY (`invoice_number`) REFERENCES `invoices`(`invoice_number`) ON DELETE CASCADE, " +
                    "FOREIGN KEY (`product_id`) REFERENCES `products`(`id`) ON DELETE CASCADE)");

            dbAvailable = true;
            System.out.println("[DATABASE] All MySQL tables verified & synchronized successfully!");
        } catch (SQLException e) {
            System.err.println("[DATABASE ERROR] Could not initialize tables: " + e.getMessage());
            dbAvailable = false;
        }
    }
}
