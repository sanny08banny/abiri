package com.example.carapp.entities;

public class CarRequest {
    private String admin_id;
    private String car_id;
    private String model;
    private String location;
    private String description;
    private String daily_price;
    private String daily_down_payment;

    // Constructor
    public CarRequest(String admin_id, String car_id, String model, String location, String description,
                      String daily_price, String daily_down_payment) {
        this.admin_id = admin_id;
        this.car_id = car_id;
        this.model = model;
        this.location = location;
        this.description = description;
        this.daily_price = daily_price;
        this.daily_down_payment = daily_down_payment;
    }

    // Getters and setters for each field

    public String getAdmin_id() {
        return admin_id;
    }

    public void setAdmin_id(String admin_id) {
        this.admin_id = admin_id;
    }

    public String getCar_id() {
        return car_id;
    }

    public void setCar_id(String car_id) {
        this.car_id = car_id;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDaily_price() {
        return daily_price;
    }

    public void setDaily_price(String daily_price) {
        this.daily_price = daily_price;
    }

    public String getDaily_down_payment() {
        return daily_down_payment;
    }

    public void setDaily_down_payment(String daily_down_payment) {
        this.daily_down_payment = daily_down_payment;
    }
}

