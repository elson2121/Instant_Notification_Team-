package com.instantnotificationsystem.dao;

import com.instantnotificationsystem.config.DBConnection;
import com.instantnotificationsystem.model.Notification;
import com.instantnotificationsystem.model.User;
import java.sql.*;
import java.util.List;

public class NotificationDAO {
    public boolean markNotificationAsSeen(int userId, int notificationId) {
        // Mark notification as seen logic
        return true;
    }

    public int createNotification(Notification notification) {
        return 1;
    }

    public void saveChannels(int notificationId, List<String> channels) {
    }

    public void createUserNotifications(int notificationId, List<User> targetUsers) {
    }

    public int getSentCount() {
        String sql = "SELECT COUNT(*) FROM notifications";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}