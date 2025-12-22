// DBConnection.java
package com.instantnotificationsystem.config;

import java.sql.*;
import java.util.Properties;

public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/instant_notification_system";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "password"; // Change this

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
        // Table creation logic from SQL above
        // This ensures tables exist when application starts
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            // Check if tables exist, create if not
            // (Implementation would check and create tables)

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