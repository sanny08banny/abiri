package com.example.carapp.review;

public class RequestCarReview {
    private String car_id;
    private String owner_id;

    public RequestCarReview(String car_id, String owner_id) {
        this.car_id = car_id;
        this.owner_id = owner_id;
    }

    public String getCar_id() {
        return car_id;
    }

    public void setCar_id(String car_id) {
        this.car_id = car_id;
    }

    public String getOwner_id() {
        return owner_id;
    }

    public void setOwner_id(String owner_id) {
        this.owner_id = owner_id;
    }
}
