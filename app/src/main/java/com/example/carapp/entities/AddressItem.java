package com.example.carapp.entities;

public class AddressItem {
    private String address;
    private double distance;

    public AddressItem(String address, double distance) {
        this.address = address;
        this.distance = distance;
    }

    public String getAddress() {
        return address;
    }

    public double getDistance() {
        return distance;
    }
}

