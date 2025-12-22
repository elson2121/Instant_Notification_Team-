package com.instantnotificationsystem.service;

import com.instantnotificationsystem.dao.UserDAO;
import com.instantnotificationsystem.model.User;

public class AuthService {
    
    private UserDAO userDAO;

    public AuthService() {
        this.userDAO = new UserDAO();
    }

    public boolean registerUser(User user) {
        if (userDAO.getUserByUsername(user.getUsername()) != null) {
            return false; // Username already exists
        }
        return userDAO.createUser(user);
    }

    public User authenticate(String username, String password) {
        User user = userDAO.getUserByUsername(username);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }
}