package com.instantnotificationsystem.model;

import java.util.List;

public class NotificationTarget {
    private int notificationId;
    private int userId;
    private boolean seen;
    private Integer departmentId;
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

    public Integer getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Integer departmentId) {
        this.departmentId = departmentId;
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