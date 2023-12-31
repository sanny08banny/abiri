package com.example.carapp.entities;

public class Ride {
    private String driver_id;
    private String user_id;
    private String start_time;
    private String driverNumber,clientNumber;
    private float driver_lat, driver_lon, client_lat, client_lon;

    public Ride() {
    }

    public Ride(String driver_id, String user_id, String start_time, String driverNumber, String clientNumber) {
        this.driver_id = driver_id;
        this.user_id = user_id;
        this.start_time = start_time;
        this.driverNumber = driverNumber;
        this.clientNumber = clientNumber;
    }

    public String getDriver_id() {
        return driver_id;
    }

    public void setDriver_id(String driver_id) {
        this.driver_id = driver_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getDriverNumber() {
        return driverNumber;
    }

    public void setDriverNumber(String driverNumber) {
        this.driverNumber = driverNumber;
    }

    public String getClientNumber() {
        return clientNumber;
    }

    public void setClientNumber(String clientNumber) {
        this.clientNumber = clientNumber;
    }

    public float getDriver_lat() {
        return driver_lat;
    }

    public void setDriver_lat(float driver_lat) {
        this.driver_lat = driver_lat;
    }

    public float getDriver_lon() {
        return driver_lon;
    }

    public void setDriver_lon(float driver_lon) {
        this.driver_lon = driver_lon;
    }

    public float getClient_lat() {
        return client_lat;
    }

    public void setClient_lat(float client_lat) {
        this.client_lat = client_lat;
    }

    public float getClient_lon() {
        return client_lon;
    }

    public void setClient_lon(float client_lon) {
        this.client_lon = client_lon;
    }
}
