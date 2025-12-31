package com.instantnotificationsystem.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class DBConnection {
    private static final Logger LOGGER = Logger.getLogger(DBConnection.class.getName());
    private static Connection connection = null;
    private static final String CONFIG_FILE = "config.properties";

    private DBConnection() {
        // private constructor to prevent instantiation
    }

    public static Connection getConnection(boolean includeDbName) throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Properties properties = new Properties();
                try (InputStream input = new FileInputStream(CONFIG_FILE)) {
                    properties.load(input);
                } catch (IOException e) {
                    LOGGER.log(Level.SEVERE, "Configuration file not found! Please check " + CONFIG_FILE + ".", e);
                    throw new SQLException("Configuration file not found!", e);
                }

                String url = properties.getProperty("db.url");
                String user = properties.getProperty("db.user");
                String password = properties.getProperty("db.password");

                if (url == null || user == null || password == null) {
                    throw new SQLException("Database credentials are not configured properly in " + CONFIG_FILE);
                }
                
                if (!includeDbName) {
                    // Connect without specifying the database name
                    url = url.substring(0, url.lastIndexOf('/'));
                }

                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(url, user, password);
            } catch (ClassNotFoundException e) {
                LOGGER.log(Level.SEVERE, "MySQL JDBC Driver not found.", e);
                throw new SQLException("MySQL JDBC Driver not found.", e);
            }
        }
        return connection;
    }

    public static Connection getConnection() throws SQLException {
        return getConnection(true);
    }

    public static void setupDatabase() {
        try (Connection conn = getConnection(false);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS notification_system");
            // Close the connection without the db name
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
            connection = null; // Reset connection to be re-established with the db name
            initializeTables();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error setting up database.", e);
        }
    }

    public static void initializeTables() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Users Table
            String createUsersTable = "CREATE TABLE IF NOT EXISTS users ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY,"
                    + "full_name VARCHAR(255) NOT NULL,"
                    + "username VARCHAR(255) NOT NULL UNIQUE,"
                    + "password VARCHAR(255) NOT NULL,"
                    + "email VARCHAR(255),"
                    + "phone_number VARCHAR(20),"
                    + "employee_id VARCHAR(50) UNIQUE,"
                    + "role VARCHAR(50),"
                    + "sex VARCHAR(10),"
                    + "shift VARCHAR(20),"
                    + "department_name VARCHAR(100),"
                    + "is_active BOOLEAN DEFAULT TRUE"
                    + ")";
            stmt.execute(createUsersTable);

            // Notifications Table
            String createNotificationsTable = "CREATE TABLE IF NOT EXISTS notifications ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY,"
                    + "title VARCHAR(255),"
                    + "message TEXT,"
                    + "sender_id INT,"
                    + "notification_type VARCHAR(50),"
                    + "send_email BOOLEAN DEFAULT FALSE,"
                    + "send_sms BOOLEAN DEFAULT FALSE,"
                    + "send_telegram BOOLEAN DEFAULT FALSE,"
                    + "send_push BOOLEAN DEFAULT FALSE,"
                    + "send_dashboard BOOLEAN DEFAULT FALSE,"
                    + "scheduled_at DATETIME,"
                    + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
                    + "FOREIGN KEY (sender_id) REFERENCES users(id)"
                    + ")";
            stmt.execute(createNotificationsTable);
            
            // User Notifications Mapping Table (Many-to-Many)
            String createUserNotificationsTable = "CREATE TABLE IF NOT EXISTS user_notifications ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY,"
                    + "user_id INT,"
                    + "notification_id INT,"
                    + "seen BOOLEAN DEFAULT FALSE,"
                    + "delivery_status VARCHAR(50) DEFAULT 'Sent',"
                    + "FOREIGN KEY (user_id) REFERENCES users(id),"
                    + "FOREIGN KEY (notification_id) REFERENCES notifications(id)"
                    + ")";
            stmt.execute(createUserNotificationsTable);

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error initializing tables.", e);
        }
    }
}