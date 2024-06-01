package com.cloudsoftware.army;


public class UserClass {
    private String email;
    private String password;
    private String username;
    private String userId;

    // Default constructor r
    public UserClass() {
    }

    // Constructor
    public UserClass(String email, String password, String username, String userId) {
        this.email = email;
        this.password = password;
        this.username = username;
        this.userId = userId;
    }

    // Getters and Setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
