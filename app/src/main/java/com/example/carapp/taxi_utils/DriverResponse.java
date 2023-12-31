package com.example.carapp.taxi_utils;

public class DriverResponse {
    private String client_id;
    private String status;

    public DriverResponse() {
    }

    public DriverResponse(String client_id, String status) {
        this.client_id = client_id;
        this.status = status;
    }

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
