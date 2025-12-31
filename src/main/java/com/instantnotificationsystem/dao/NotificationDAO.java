package com.instantnotificationsystem.dao;

import com.instantnotificationsystem.config.DBConnection;
import com.instantnotificationsystem.model.Analytics;
import com.instantnotificationsystem.model.Notification;
import com.instantnotificationsystem.model.User;
import com.instantnotificationsystem.model.UserNotificationDetail;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificationDAO {

    public int createNotification(Notification notification) {
        if (notification.getSenderId() <= 0) {
            throw new IllegalStateException("Sender ID is invalid. A real user must be logged in.");
        }
        // Modified to exclude channel columns which are missing in the DB
        String sql = "INSERT INTO notifications (title, message, notification_type, sender_id) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, notification.getTitle());
            pstmt.setString(2, notification.getMessage());
            pstmt.setString(3, notification.getNotificationType());
            pstmt.setInt(4, notification.getSenderId());
            
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
        String sql = "SELECT n.*, " +
                     "(SELECT COUNT(*) FROM user_notifications un JOIN users u ON un.user_id = u.id WHERE un.notification_id = n.id AND un.seen = TRUE AND u.is_active = TRUE) as seen_count, " +
                     "(SELECT COUNT(*) FROM user_notifications un JOIN users u ON un.user_id = u.id WHERE un.notification_id = n.id AND u.is_active = TRUE) as total_recipients " +
                     "FROM notifications n ORDER BY created_at DESC";
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
                notification.setSeenCount(rs.getInt("seen_count"));
                notification.setTotalRecipients(rs.getInt("total_recipients"));
                notifications.add(notification);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return notifications;
    }
    
    public List<Notification> getNotificationsBySeenStatus(boolean isSeen) {
        List<Notification> notifications = new ArrayList<>();
        String sql = "SELECT n.* FROM notifications n JOIN user_notifications un ON n.id = un.notification_id JOIN users u ON un.user_id = u.id WHERE un.seen = ? AND u.is_active = TRUE ORDER BY n.created_at DESC";
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

    public List<UserNotificationDetail> getUserNotificationDetailsBySeenStatus(boolean isSeen) {
        List<UserNotificationDetail> details = new ArrayList<>();
        String sql = "SELECT u.full_name, n.title, un.seen " +
                     "FROM user_notifications un " +
                     "JOIN users u ON un.user_id = u.id " +
                     "JOIN notifications n ON un.notification_id = n.id " +
                     "WHERE un.seen = ? AND u.is_active = TRUE " +
                     "ORDER BY n.created_at DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setBoolean(1, isSeen);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                String userName = rs.getString("full_name");
                String title = rs.getString("title");
                boolean seen = rs.getBoolean("seen");
                String status = seen ? "Seen" : "Unseen";
                
                details.add(new UserNotificationDetail(userName, title, status));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return details;
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
        String sql = "SELECT COUNT(*) FROM user_notifications un JOIN users u ON un.user_id = u.id WHERE un.seen = ? AND u.is_active = TRUE";
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

    public List<Notification> getUnreadNotificationsForUser(int userId) {
        return getNotificationsForUserBySeenStatus(userId, false);
    }

    public List<Notification> getReadNotificationsForUser(int userId) {
        return getNotificationsForUserBySeenStatus(userId, true);
    }

    private List<Notification> getNotificationsForUserBySeenStatus(int userId, boolean seen) {
        List<Notification> notifications = new ArrayList<>();
        // Modified to exclude channel columns which are missing in the DB
        String sql = "SELECT n.id, n.title, n.message, n.created_at, un.seen, n.notification_type " +
                     "FROM notifications n " +
                     "JOIN user_notifications un ON n.id = un.notification_id " +
                     "JOIN users u ON u.id = un.user_id " +
                     "WHERE un.user_id = ? AND u.is_active = TRUE AND un.seen = ? " +
                     "ORDER BY n.created_at DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            pstmt.setBoolean(2, seen);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Notification notification = new Notification();
                notification.setId(rs.getInt("id"));
                notification.setTitle(rs.getString("title"));
                notification.setMessage(rs.getString("message"));
                notification.setSentAt(rs.getTimestamp("created_at").toLocalDateTime());
                notification.setSeen(rs.getBoolean("seen"));
                notification.setNotificationType(rs.getString("notification_type"));

                // Channels are not available in DB, so we set an empty list
                notification.setChannels(new ArrayList<>());
                
                notifications.add(notification);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return notifications;
    }

    public boolean markNotificationAsSeen(int userId, int notificationId) {
        // Modified to exclude seen_at column which is missing in the DB
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
        // Modified to exclude seen_at column which is missing in the DB
        String sql = "UPDATE user_notifications SET seen = TRUE WHERE user_id = ? AND seen = FALSE";
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
        String sql = "SELECT COUNT(*) FROM user_notifications un " +
                     "JOIN notifications n ON un.notification_id = n.id " +
                     "JOIN users u ON u.id = un.user_id " +
                     "WHERE un.user_id = ? AND u.is_active = TRUE AND un.seen = FALSE";
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
                     "(SELECT COUNT(*) FROM user_notifications un JOIN users u ON un.user_id = u.id WHERE un.seen = TRUE AND u.is_active = TRUE) AS seen_count, " +
                     "(SELECT COUNT(*) FROM user_notifications un JOIN users u ON un.user_id = u.id WHERE un.seen = FALSE AND u.is_active = TRUE) AS unseen_count";
        
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
    
    public boolean deleteNotificationForUser(int userId, int notificationId) {
    String sql = "DELETE FROM user_notifications WHERE user_id = ? AND notification_id = ?";
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
}