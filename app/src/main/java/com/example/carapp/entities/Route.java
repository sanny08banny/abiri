package com.example.carapp.entities;

import java.util.List;

public class Route {
    private List<Leg> legs;

    // Getters and setters

    public List<Leg> getLegs() {
        return legs;
    }

    public void setLegs(List<Leg> legs) {
        this.legs = legs;
    }
}
