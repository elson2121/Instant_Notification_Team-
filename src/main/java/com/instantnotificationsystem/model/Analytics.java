package com.instantnotificationsystem.model;

/**
 * A data transfer object (DTO) to hold notification analytics counts.
 * This provides a clean, type-safe way to pass multiple statistics
 * from the DAO to the controller in a single method call.
 */
public class Analytics {

    private final int sentCount;
    private final int deliveredCount;
    private final int seenCount;
    private final int unseenCount;

    public Analytics(int sentCount, int deliveredCount, int seenCount, int unseenCount) {
        this.sentCount = sentCount;
        this.deliveredCount = deliveredCount;
        this.seenCount = seenCount;
        this.unseenCount = unseenCount;
    }

    // Getters
    public int getSentCount() {
        return sentCount;
    }

    public int getDeliveredCount() {
        return deliveredCount;
    }

    public int getSeenCount() {
        return seenCount;
    }

    public int getUnseenCount() {
        return unseenCount;
    }
}