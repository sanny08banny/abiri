package com.example.carapp.entities;

public class AdminAccessRequest {
    private String user_id;
    private String category;

    public AdminAccessRequest(String user_id, String category) {
        this.user_id = user_id;
        this.category = category;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
