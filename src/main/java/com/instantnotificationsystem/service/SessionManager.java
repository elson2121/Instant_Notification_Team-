package com.instantnotificationsystem.service;

import com.instantnotificationsystem.model.User;

/**
 * A simple static class to manage the currently logged-in user session.
 * This allows any part of the application to access the user's data
 * without having to pass it between controllers.
 */
public class SessionManager {

    private static User loggedInUser;

    /**
     * Sets the user for the current session upon successful login.
     * @param user The user object retrieved from the database.
     */
    public static void setUser(User user) {
        loggedInUser = user;
    }

    /**
     * Retrieves the currently logged-in user.
     * @return The logged-in User object, or null if no user is logged in.
     */
    public static User getLoggedInUser() {
        return loggedInUser;
    }

    /**
     * Clears the session data upon logout.
     */
    public static void clear() {
        loggedInUser = null;
    }
}