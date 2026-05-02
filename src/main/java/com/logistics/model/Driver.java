package com.logistics.model;

/**
 * Represents a driver in the logistics system.
 */
public class Driver {
    private int id;
    private String name;
    private String vehicle;

    public Driver(int id, String name, String vehicle) {
        this.id = id;
        this.name = name;
        this.vehicle = vehicle;
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getVehicle() { return vehicle; }

    @Override
    public String toString() {
        return "ID: " + id + " | Name: " + name + " | Vehicle: " + vehicle;
    }
}
