package com.instantnotificationsystem.model;

public class User {
    private int id;
    private String fullName;
    private String username;
    private String password;
    private String role;
    private String department;
    private String phoneNumber;
    private String email;
    private String sex;
    private String shift;
    private boolean isActive;
    private String employeeId; // Employee ID field added

    // Constructors
    public User() {
    }

    public User(int id, String fullName, String username, String password, String role,
                String department, String phoneNumber, String email,
                String sex, String shift, boolean isActive, String employeeId) {
        this.id = id;
        this.fullName = fullName;
        this.username = username;
        this.password = password;
        this.role = role;
        this.department = department;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.sex = sex;
        this.shift = shift;
        this.isActive = isActive;
        this.employeeId = employeeId; // Employee ID initialized
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", fullName='" + fullName + '\'' +
                ", username='" + username + '\'' +
                ", role='" + role + '\'' +
                ", department='" + department + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", isActive=" + isActive +
                ", employeeId='" + employeeId + '\'' +
                '}';
    }
}