package com.example.carapp.entities;

public class UserLoginResponse {
    private String user_id;
    private Boolean is_admin;
    private Boolean is_driver;

    public UserLoginResponse() {
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public Boolean getIs_admin() {
        return is_admin;
    }

    public void setIs_admin(Boolean is_admin) {
        this.is_admin = is_admin;
    }

    public Boolean getIs_driver() {
        return is_driver;
    }

    public void setIs_driver(Boolean is_driver) {
        this.is_driver = is_driver;
    }
}
