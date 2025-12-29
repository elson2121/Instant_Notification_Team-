package com.instantnotificationsystem.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

@SuppressWarnings("checkstyle:HideUtilityClassConstructor")
public final class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/instant_notification_system";
    private static final String USER = "root";
    private static final String PASSWORD = "system";

    private static Connection connection = null;
    private static final Logger LOGGER = Logger.getLogger(DBConnection.class.getName());

    private DBConnection() {
        // private constructor to prevent instantiation
    }

    public static Connection getConnection() throws SQLException {
        // Always create a new connection to avoid closed connection issues in multi-threaded environments or long sessions
        // Connection pooling (like HikariCP) is recommended for production, but for this scope, creating new connections is safer than reusing a closed static one.
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "MySQL JDBC Driver not found.", e);
            throw new SQLException("MySQL JDBC Driver not found.", e);
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
                    + "channels VARCHAR(255),"
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