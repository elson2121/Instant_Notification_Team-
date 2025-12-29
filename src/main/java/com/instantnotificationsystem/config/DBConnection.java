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
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
            } catch (ClassNotFoundException e) {
                LOGGER.log(Level.SEVERE, "MySQL JDBC Driver not found.", e);
                throw new SQLException("MySQL JDBC Driver not found.", e);
            }
        }
        return connection;
    }

    public static void initializeTables() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            String createUsersTable = "CREATE TABLE IF NOT EXISTS users ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY,"
                    + "username VARCHAR(255) NOT NULL UNIQUE,"
                    + "password VARCHAR(255) NOT NULL"
                    + ")";
            stmt.execute(createUsersTable);

            String createNotificationsTable = "CREATE TABLE IF NOT EXISTS notifications ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY,"
                    + "user_id INT,"
                    + "message TEXT,"
                    + "is_read BOOLEAN DEFAULT FALSE,"
                    + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
                    + "FOREIGN KEY (user_id) REFERENCES users(id)"
                    + ")";
            stmt.execute(createNotificationsTable);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error initializing tables.", e);
        }
    }
}