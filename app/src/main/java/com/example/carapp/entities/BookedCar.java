package com.example.carapp.entities;

public class BookedCar {
    private String car_id;
    private String duration,owner_id,pricing;
    private String image;

    public BookedCar() {
    }

    public BookedCar(String car_id, String duration, String owner_id, String pricing, String image) {
        this.car_id = car_id;
        this.duration = duration;
        this.owner_id = owner_id;
        this.pricing = pricing;
        this.image = image;
    }

    public String getCar_id() {
        return car_id;
    }

    public void setCar_id(String car_id) {
        this.car_id = car_id;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getOwner_id() {
        return owner_id;
    }

    public void setOwner_id(String owner_id) {
        this.owner_id = owner_id;
    }

    public String getPricing() {
        return pricing;
    }

    public void setPricing(String pricing) {
        this.pricing = pricing;
    }
}
