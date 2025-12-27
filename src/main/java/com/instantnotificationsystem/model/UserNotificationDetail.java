package com.instantnotificationsystem.model;

public class UserNotificationDetail {
    private String userName;
    private String notificationTitle;
    private String status; // "Seen" or "Unseen"

    public UserNotificationDetail(String userName, String notificationTitle, String status) {
        this.userName = userName;
        this.notificationTitle = notificationTitle;
        this.status = status;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getNotificationTitle() {
        return notificationTitle;
    }

    public void setNotificationTitle(String notificationTitle) {
        this.notificationTitle = notificationTitle;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
