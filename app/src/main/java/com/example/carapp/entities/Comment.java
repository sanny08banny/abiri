package com.example.carapp.entities;

public class Comment {

    private String user_name;
    private float rating;
    private String title;
    private String comment;

    // Constructors, getters, and setters

    public Comment(String userName, float rating, String title, String comment) {
        this.user_name = userName;
        this.rating = rating;
        this.title = title;
        this.comment = comment;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
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
}

