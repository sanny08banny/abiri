package com.example.carapp.review;

import com.example.carapp.entities.Comment;

import java.util.List;

public class CarReviewResponse {

    private String carId;
    private String ownerId;
    private double averageRating;
    private List<Comment> comments;

    // Constructors, getters, and setters

    public CarReviewResponse(String carId, String ownerId, double averageRating, List<Comment> comments) {
        this.carId = carId;
        this.ownerId = ownerId;
        this.averageRating = averageRating;
        this.comments = comments;
    }

    public String getCarId() {
        return carId;
    }

    public void setCarId(String carId) {
        this.carId = carId;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }
}

