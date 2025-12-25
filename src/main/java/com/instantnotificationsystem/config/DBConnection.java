// DBConnection.java
package com.instantnotificationsystem.config;

import java.sql.*;
import java.util.Properties;

public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3307/instant_notification_system";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "Compa@2017#2025"; // Change this

    private static Connection connection = null;

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                Properties connectionProps = new Properties();
                connectionProps.put("user", USERNAME);
                connectionProps.put("password", PASSWORD);
                connectionProps.put("useSSL", "false");
                connectionProps.put("serverTimezone", "UTC");
                connectionProps.put("allowPublicKeyRetrieval", "true");

                connection = DriverManager.getConnection(URL, connectionProps);
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.err.println("Database connection error: " + e.getMessage());
            throw new RuntimeException("Failed to connect to database", e);
        }
        return connection;
    }

    public static boolean testConnection() {
        try {
            getConnection();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static void initializeTables() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            String createUsersTable = "CREATE TABLE IF NOT EXISTS users (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "full_name VARCHAR(100) NOT NULL, " +
                    "username VARCHAR(50) NOT NULL UNIQUE, " +
                    "password VARCHAR(255) NOT NULL, " +
                    "phone_number VARCHAR(20), " +
                    "employee_id VARCHAR(20), " +
                    "role VARCHAR(20) NOT NULL, " +
                    "sex VARCHAR(10), " +
                    "shift VARCHAR(20), " +
                    "department_name VARCHAR(50)" +
                    ")";
            stmt.execute(createUsersTable);

            // Check for missing columns and add them if necessary
            DatabaseMetaData meta = conn.getMetaData();
            
            // Check department_name
            try (ResultSet rsCol = meta.getColumns(null, null, "users", "department_name")) {
                if (!rsCol.next()) {
                    System.out.println("Adding missing column department_name to users table...");
                    stmt.execute("ALTER TABLE users ADD COLUMN department_name VARCHAR(50)");
                }
            }

            // Create default admin if not exists
            String checkAdmin = "SELECT COUNT(*) FROM users WHERE username = 'admin'";
            ResultSet rs = stmt.executeQuery(checkAdmin);
            if (rs.next() && rs.getInt(1) == 0) {
                String insertAdmin = "INSERT INTO users (full_name, username, password, role, department_name) " +
                        "VALUES ('System Administrator', 'admin', 'admin', 'ADMIN', 'IT')";
                stmt.execute(insertAdmin);
            }

        } catch (SQLException e) {
            System.err.println("Table initialization error: " + e.getMessage());
        }
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }

    // Helper method for transactions
    public static void executeTransaction(Runnable databaseOperations) throws SQLException {
        Connection conn = getConnection();
        boolean originalAutoCommit = conn.getAutoCommit();

        try {
            conn.setAutoCommit(false);
            databaseOperations.run();
            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(originalAutoCommit);
        }
    }
}