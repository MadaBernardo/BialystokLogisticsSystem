package com.logistics.model;

/**
 * Represents a single delivery task in the Bialystok region.
 * This is the "Blueprint" or "Value Object" of our system.
 */
public class Delivery {
    // Attributes (Private to ensure Encapsulation)
    private final int id;
    private final String destinationAddress;
    private final double weight; // In kilograms
    private boolean isDelivered;

    /**
     * Constructor: Initializes a new Delivery object.
     * Note: isDelivered starts as 'false' by default.
     */
    public Delivery(int id, String destinationAddress, double weight) {
        this.id = id;
        this.destinationAddress = destinationAddress;
        this.weight = weight;
        this.isDelivered = false;
    }

    // --- Getters (The "Windows" that allow other classes to read the data) ---

    public int getId() {
        return id;
    }

    public String getDestinationAddress() {
        return destinationAddress;
    }

    /**
     * It allows the View to display the weight in the table.
     */
    public double getWeight() {
        return weight;
    }

    /**
     * For booleans, we use "is" instead of "get".
     */
    public boolean isDelivered() {
        return isDelivered;
    }

    // --- Setters (Allows changing the state of the delivery) ---

    public void setDelivered(boolean delivered) {
        isDelivered = delivered;
    }

    @Override
    /**
     * Returns a string representation of the delivery for debugging purposes.
     */
    public String toString() {
        return "Delivery #" + id + " to " + destinationAddress +
                " [" + (isDelivered ? "Done" : "Pending") + "]";
    }
}