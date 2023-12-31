package com.example.carapp.entities;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Car implements Parcelable {
    private ArrayList<String> car_images;
    private String model;
    private String car_id;
    private String owner_id;
    private String location;
    private String description;
    private double amount;
    private double downpayment_amt;
    private String available;

    public Car() {
    }

    public Car(ArrayList<String> car_images, String model, String car_id, String owner_id, String location, String description, double amount, double downpayment_amt, String available) {
        this.car_images = car_images;
        this.model = model;
        this.car_id = car_id;
        this.owner_id = owner_id;
        this.location = location;
        this.description = description;
        this.amount = amount;
        this.downpayment_amt = downpayment_amt;
        this.available = available;
    }

    protected Car(Parcel in) {
        car_images = in.createStringArrayList();
        model = in.readString();
        car_id = in.readString();
        owner_id = in.readString();
        location = in.readString();
        description = in.readString();
        amount = in.readDouble();
        downpayment_amt = in.readDouble();
        available = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringList(car_images);
        dest.writeString(model);
        dest.writeString(car_id);
        dest.writeString(owner_id);
        dest.writeString(location);
        dest.writeString(description);
        dest.writeDouble(amount);
        dest.writeDouble(downpayment_amt);
        dest.writeString(available);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Car> CREATOR = new Creator<Car>() {
        @Override
        public Car createFromParcel(Parcel in) {
            return new Car(in);
        }

        @Override
        public Car[] newArray(int size) {
            return new Car[size];
        }
    };

    public ArrayList<String> getCar_images() {
        return car_images;
    }

    public void setCar_images(ArrayList<String> car_images) {
        this.car_images = car_images;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
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

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getDownpayment_amt() {
        return downpayment_amt;
    }

    public void setDownpayment_amt(double downpayment_amt) {
        this.downpayment_amt = downpayment_amt;
    }

    public String getAvailable() {
        return available;
    }

    public void setAvailable(String available) {
        this.available = available;
    }
}
