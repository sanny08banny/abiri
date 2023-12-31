package com.example.carapp.messagingUtils;

public class OnlineStatus {
    private String last_updated;
    private boolean status;
    private String userId;

    public OnlineStatus(String last_updated, boolean status, String userId) {
        this.last_updated = last_updated;
        this.status = status;
        this.userId = userId;
    }

    // Getter and Setter methods for the fields
    public String getLast_updated() {
        return last_updated;
    }

    public void setLast_updated(String last_updated) {
        this.last_updated = last_updated;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}

