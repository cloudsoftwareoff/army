package com.cloudsoftware.army.models;


public class UserStatus {
    private String userId;
    //"Eligible", "Court", "Warrant", "Studying"

    private String status;
    private int notifCount;

    public UserStatus() {
        // Default constructor required for calls to DataSnapshot.getValue(UserStatus.class)
    }

    public UserStatus(String userId, String status, int notifCount) {
        this.userId = userId;
        this.status = status;
        this.notifCount = notifCount;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getNotifCount() {
        return notifCount;
    }

    public void setNotifCount(int notifCount) {
        this.notifCount = notifCount;
    }
}
