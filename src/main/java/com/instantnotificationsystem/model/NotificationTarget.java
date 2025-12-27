package com.instantnotificationsystem.model;

import java.util.List;

public class NotificationTarget {
    private int notificationId;
    private int userId;
    private boolean seen;
    private String department; // Changed from Integer departmentId to String department
    private String role; // Added role field
    private String sex;
    private String shift;
    private List<Integer> specificUserIds;

    public int getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(int notificationId) {
        this.notificationId = notificationId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getShift() {
        return shift;
    }

    public void setShift(String shift) {
        this.shift = shift;
    }

    public List<Integer> getSpecificUserIds() {
        return specificUserIds;
    }

    public void setSpecificUserIds(List<Integer> specificUserIds) {
        this.specificUserIds = specificUserIds;
    }
}