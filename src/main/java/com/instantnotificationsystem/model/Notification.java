package com.instantnotificationsystem.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

public class Notification {
    private int id;
    private String title;
    private String message;
    private boolean seen;
    private LocalDateTime sentAt; // Represents created_at
    private LocalDateTime scheduledAt; // New field for scheduled delivery
    private List<String> channels = new ArrayList<>();
    private int senderId;
    private String notificationType;
    private String status; // New field for status
    private int seenCount;
    private int totalRecipients;

    // Getters and Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }

    public LocalDateTime getScheduledAt() {
        return scheduledAt;
    }

    public void setScheduledAt(LocalDateTime scheduledAt) {
        this.scheduledAt = scheduledAt;
    }

    public List<String> getChannels() {
        return channels;
    }

    public void setChannels(List<String> channels) {
        this.channels = channels;
    }

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }

    public String getStatus() {
        // Mock logic to determine status. In a real scenario, this would be read from the database.
        if (status == null) {
            // Simulate some logic, e.g., based on whether it's seen or not
            if (isSeen()) {
                return "Delivered";
            } else {
                return "Sent";
            }
        }
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getSeenCount() {
        return seenCount;
    }

    public void setSeenCount(int seenCount) {
        this.seenCount = seenCount;
    }

    public int getTotalRecipients() {
        return totalRecipients;
    }

    public void setTotalRecipients(int totalRecipients) {
        this.totalRecipients = totalRecipients;
    }
}