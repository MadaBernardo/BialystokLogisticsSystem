package com.logistics.controller;

import com.logistics.model.Delivery;
import java.util.ArrayList;
import java.util.List;
import java.io.*;
import java.util.Scanner;

/**
 * The Manager of our system.
 * It handles the "Database" (a List), persistent storage, and ID generation.
 */
public class LogisticsController {

    private List<Delivery> deliveries;

    /** * Keeps track of the absolute next ID to be assigned.
     * This prevents ID duplication even when items are deleted.
     */
    private int nextId = 1;

    public LogisticsController() {
        this.deliveries = new ArrayList<>();
    }

    /**
     * Creates and adds a new delivery to the system.
     * Uses a dedicated counter for IDs to ensure uniqueness.
     */
    public void addDelivery(String address, double weight) {
        // Use nextId and increment it immediately for the next call
        Delivery d = new Delivery(nextId++, address, weight);
        deliveries.add(d);
    }

    public List<Delivery> getAllDeliveries() {
        return deliveries;
    }

    public void removeDelivery(int index) {
        if (index >= 0 && index < deliveries.size()) {
            deliveries.remove(index);
        }
    }

    /**
     * Saves all current deliveries to a CSV file.
     */
    public void saveToFile(String filename) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            for (Delivery d : deliveries) {
                writer.println(d.getId() + ";" +
                        d.getDestinationAddress() + ";" +
                        d.getWeight() + ";" +
                        d.isDelivered());
            }
        } catch (IOException e) {
            System.err.println("Error saving to file: " + e.getMessage());
        }
    }

    /**
     * Loads deliveries from a CSV file.
     * Also recovers the 'nextId' state based on the highest ID found.
     */
    public void loadFromFile(String filename) {
        File file = new File(filename);
        if (!file.exists()) return;

        int maxIdFound = 0; // Temporary variable to find the highest existing ID

        try (Scanner scanner = new Scanner(file)) {
            deliveries.clear();
            while (scanner.hasNextLine()) {
                String[] parts = scanner.nextLine().split(";");
                if (parts.length == 4) {
                    int id = Integer.parseInt(parts[0]);

                    Delivery d = new Delivery(id, parts[1], Double.parseDouble(parts[2]));
                    d.setDelivered(Boolean.parseBoolean(parts[3]));
                    deliveries.add(d);

                    // Track the highest ID found in the file
                    if (id > maxIdFound) {
                        maxIdFound = id;
                    }
                }
            }
            // Ensure the next new delivery starts after the highest existing ID
            this.nextId = maxIdFound + 1;

        } catch (FileNotFoundException e) {
            // Standard practice: log error or handle silently if file is missing
        }
    }
}
