package com.example.carapp.review;

public class Review {

    private String user_id;
    private String car_id;
    private String owner_id;
    private String title;
    private String comment;
    private float rating;
    private String review_id;

    // Constructor
    public Review(String userId, String carId, String ownerId, String title, String comment, float rating, String reviewId) {
        this.user_id = userId;
        this.car_id = carId;
        this.owner_id = ownerId;
        this.title = title;
        this.comment = comment;
        this.rating = rating;
        this.review_id = reviewId;
    }

    // Getters and Setters

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getReview_id() {
        return review_id;
    }

    public void setReview_id(String review_id) {
        this.review_id = review_id;
    }

    // toString method for easy logging or debugging
    @Override
    public String toString() {
        return "Review{" +
                "userId='" + user_id + '\'' +
                ", carId='" + car_id + '\'' +
                ", ownerId='" + owner_id + '\'' +
                ", title='" + title + '\'' +
                ", comment='" + comment + '\'' +
                ", rating=" + rating +
                ", reviewId='" + review_id + '\'' +
                '}';
    }
}

