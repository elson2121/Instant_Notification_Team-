package com.instantnotificationsystem.model;

public class Analytics {
    private int totalNotifications;
    private int seenNotifications;

    public int getTotalNotifications() {
        return totalNotifications;
    }

    public void setTotalNotifications(int totalNotifications) {
        this.totalNotifications = totalNotifications;
    }

    public int getSeenNotifications() {
        return seenNotifications;
    }

    public void setSeenNotifications(int seenNotifications) {
        this.seenNotifications = seenNotifications;
    }
}