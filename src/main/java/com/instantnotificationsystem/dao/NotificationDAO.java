package com.instantnotificationsystem.dao;

import com.instantnotificationsystem.config.DBConnection;
import com.instantnotificationsystem.model.Analytics;
import com.instantnotificationsystem.model.Notification;
import com.instantnotificationsystem.model.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificationDAO {

    public int createNotification(Notification notification) {
        if (notification.getSenderId() <= 0) {
            throw new IllegalStateException("Sender ID is invalid. A real user must be logged in.");
        }
        String sql = "INSERT INTO notifications (title, message, send_email, send_sms, notification_type, sender_id) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            boolean sendEmail = notification.getChannels().contains("Email");
            boolean sendSms = notification.getChannels().contains("SMS");

            pstmt.setString(1, notification.getTitle());
            pstmt.setString(2, notification.getMessage());
            pstmt.setBoolean(3, sendEmail);
            pstmt.setBoolean(4, sendSms);
            pstmt.setString(5, notification.getNotificationType());
            pstmt.setInt(6, notification.getSenderId());
            
            if (pstmt.executeUpdate() > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void createUserNotifications(int notificationId, List<User> targetUsers) {
        String sql = "INSERT INTO user_notifications (user_id, notification_id, seen) VALUES (?, ?, FALSE)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (User user : targetUsers) {
                pstmt.setInt(1, user.getId());
                pstmt.setInt(2, notificationId);
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Notification> getAllNotifications() {
        List<Notification> notifications = new ArrayList<>();
        String sql = "SELECT * FROM notifications ORDER BY created_at DESC";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Notification notification = new Notification();
                notification.setId(rs.getInt("id"));
                notification.setTitle(rs.getString("title"));
                notification.setMessage(rs.getString("message"));
                notification.setNotificationType(rs.getString("notification_type"));
                Timestamp timestamp = rs.getTimestamp("created_at");
                if (timestamp != null) {
                    notification.setSentAt(timestamp.toLocalDateTime());
                }
                notifications.add(notification);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return notifications;
    }
    
    public List<Notification> getNotificationsBySeenStatus(boolean isSeen) {
        List<Notification> notifications = new ArrayList<>();
        String sql = "SELECT n.* FROM notifications n JOIN user_notifications un ON n.id = un.notification_id WHERE un.seen = ? ORDER BY n.created_at DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setBoolean(1, isSeen);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Notification notification = new Notification();
                notification.setId(rs.getInt("id"));
                notification.setTitle(rs.getString("title"));
                notification.setMessage(rs.getString("message"));
                notification.setNotificationType(rs.getString("notification_type"));
                Timestamp timestamp = rs.getTimestamp("created_at");
                if (timestamp != null) {
                    notification.setSentAt(timestamp.toLocalDateTime());
                }
                notifications.add(notification);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return notifications;
    }

    public List<Notification> getNotificationsByDeliveryStatus(String status) {
        // This is a mock implementation. A real implementation would require a 'status' column in the notifications table.
        List<Notification> allNotifications = getAllNotifications();
        if ("Delivered".equalsIgnoreCase(status)) {
            return allNotifications.subList(0, (int) (allNotifications.size() * 0.9));
        }
        return new ArrayList<>();
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

    public int getCountBySeenStatus(boolean isSeen) {
        String sql = "SELECT COUNT(*) FROM user_notifications WHERE seen = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setBoolean(1, isSeen);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getCountByDeliveryStatus(String status) {
        // Mock implementation
        if ("Delivered".equalsIgnoreCase(status)) {
            return (int) (getSentCount() * 0.9);
        }
        return 0;
    }

    public List<Notification> getNotificationsForUser(int userId) {
        List<Notification> notifications = new ArrayList<>();
        String sql = "SELECT n.id, n.title, n.message, n.created_at, un.seen, n.notification_type, n.send_email, n.send_sms " +
                     "FROM notifications n JOIN user_notifications un ON n.id = un.notification_id " +
                     "WHERE un.user_id = ? ORDER BY n.created_at DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Notification notification = new Notification();
                notification.setId(rs.getInt("id"));
                notification.setTitle(rs.getString("title"));
                notification.setMessage(rs.getString("message"));
                notification.setSentAt(rs.getTimestamp("created_at").toLocalDateTime());
                notification.setSeen(rs.getBoolean("seen"));
                notification.setNotificationType(rs.getString("notification_type"));

                List<String> channels = new ArrayList<>();
                if (rs.getBoolean("send_email")) {
                    channels.add("Email");
                }
                if (rs.getBoolean("send_sms")) {
                    channels.add("SMS");
                }
                notification.setChannels(channels);
                
                notifications.add(notification);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return notifications;
    }

    public boolean markNotificationAsSeen(int userId, int notificationId) {
        String sql = "UPDATE user_notifications SET seen = TRUE WHERE user_id = ? AND notification_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, notificationId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean markAllNotificationsAsSeen(int userId) {
        String sql = "UPDATE user_notifications SET seen = TRUE WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public int getUnreadNotificationCount(int userId) {
        String sql = "SELECT COUNT(*) FROM user_notifications WHERE user_id = ? AND seen = FALSE";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

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
        return new Analytics(0, 0, 0, 0);
    }
}