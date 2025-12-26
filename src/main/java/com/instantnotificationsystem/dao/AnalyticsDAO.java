package com.instantnotificationsystem.dao;

import com.instantnotificationsystem.config.DBConnection;
import com.instantnotificationsystem.model.Analytics;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class AnalyticsDAO {

    /**
     * Fetches all key notification statistics in a single, efficient database call.
     * @return An Analytics object containing all counts.
     */
    public Analytics getNotificationAnalytics() {
        String sql = "SELECT " +
                     "(SELECT COUNT(*) FROM notifications) AS sent_count, " +
                     "(SELECT COUNT(*) FROM notifications WHERE status = 'Delivered') AS delivered_count, " +
                     "(SELECT COUNT(*) FROM user_notifications WHERE seen = TRUE) AS seen_count, " +
                     "(SELECT COUNT(*) FROM user_notifications WHERE seen = FALSE) AS unseen_count";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                // Correctly call the constructor with all required arguments
                return new Analytics(
                    rs.getInt("sent_count"),
                    rs.getInt("delivered_count"),
                    rs.getInt("seen_count"),
                    rs.getInt("unseen_count")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // On failure, return a zero-value object using the correct constructor
        return new Analytics(0, 0, 0, 0);
    }
}