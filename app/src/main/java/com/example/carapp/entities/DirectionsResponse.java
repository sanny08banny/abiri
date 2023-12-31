package com.example.carapp.entities;

import java.util.List;

public class DirectionsResponse {
    private List<Route> routes;

    // Getters and setters

    public List<Route> getRoutes() {
        return routes;
    }

    public void setRoutes(List<Route> routes) {
        this.routes = routes;
    }
}

