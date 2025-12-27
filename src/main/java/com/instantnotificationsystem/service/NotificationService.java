// NotificationService.java
package com.instantnotificationsystem.service;

import com.instantnotificationsystem.dao.*;
import com.instantnotificationsystem.model.*;
import java.time.LocalDateTime;
import java.util.List;

public class NotificationService {
    private NotificationDAO notificationDAO;
    private UserDAO userDAO;

    public NotificationService() {
        this.notificationDAO = new NotificationDAO();
        this.userDAO = new UserDAO();
    }

    public NotificationResult sendNotification(Notification notification,
                                               NotificationTarget target,
                                               List<String> channels) {
        NotificationResult result = new NotificationResult();

        try {
            // 1. Get target users based on criteria
            List<User> targetUsers = resolveTargetUsers(target);
            if (targetUsers.isEmpty()) {
                result.setSuccess(false);
                result.setMessage("No users match the target criteria");
                return result;
            }

            // 2. Save notification to database and get the new ID
            int notificationId = notificationDAO.createNotification(notification);
            if (notificationId <= 0) {
                result.setSuccess(false);
                result.setMessage("Failed to save notification");
                return result;
            }

            // 3. Link the notification to the target users
            notificationDAO.createUserNotifications(notificationId, targetUsers);

            // 4. Simulate delivery through channels
            if (notification.getSentAt() == null ||
                    notification.getSentAt().isBefore(LocalDateTime.now())) {
                deliverNotification(notification, targetUsers, channels);
            }

            result.setSuccess(true);
            result.setNotificationId(notificationId);
            result.setMessage("Notification sent to " + targetUsers.size() + " users");
            result.setTargetCount(targetUsers.size());

        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage("Error sending notification: " + e.getMessage());
        }

        return result;
    }

    private List<User> resolveTargetUsers(NotificationTarget target) {
        return userDAO.getUsersByCriteria(
                target.getDepartment(),
                target.getRole(),
                target.getSex(),
                target.getShift()
        );
    }

    private void deliverNotification(Notification notification,
                                     List<User> users,
                                     List<String> channels) {
        for (String channel : channels) {
            switch (channel) {
                case "SMS":
                    simulateSMSDelivery(notification, users);
                    break;
                case "Email": // Corrected to match checkbox text
                    simulateEmailDelivery(notification, users);
                    break;
                case "TELEGRAM":
                    simulateTelegramDelivery(notification, users);
                    break;
                case "DASHBOARD":
                    // Dashboard notifications are already available
                    System.out.println("✓ Dashboard notification ready for " + users.size() + " users");
                    break;
                case "PUSH":
                    simulatePushDelivery(notification, users);
                    break;
            }
        }
    }

    private void simulateSMSDelivery(Notification notification, List<User> users) {
        System.out.println("📱 Sending SMS to " + users.size() + " users:");
        for (User user : users) {
            System.out.println("  → To: " + user.getPhoneNumber() +
                    " | Message: " + notification.getTitle());
        }
    }

    private void simulateEmailDelivery(Notification notification, List<User> users) {
        System.out.println("📧 Sending Email to " + users.size() + " users");
    }

    private void simulateTelegramDelivery(Notification notification, List<User> users) {
        System.out.println("✈️ Sending Telegram to " + users.size() + " users");
    }

    private void simulatePushDelivery(Notification notification, List<User> users) {
        System.out.println("🔔 Sending Push Notification to " + users.size() + " users");
    }

    // Inner class for notification result
    public static class NotificationResult {
        private boolean success;
        private String message;
        private int notificationId;
        private int targetCount;

        // Getters and setters
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public int getNotificationId() { return notificationId; }
        public void setNotificationId(int notificationId) { this.notificationId = notificationId; }
        public int getTargetCount() { return targetCount; }
        public void setTargetCount(int targetCount) { this.targetCount = targetCount; }
    }
}