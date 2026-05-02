package com.logistics.controller;

import com.logistics.model.Delivery;
import com.logistics.model.Driver;
import java.util.ArrayList;
import java.util.List;
import java.io.*;
import java.util.Scanner;

/**
 * The core logic manager. Handles data lists, ID generation, and CSV persistence.
 */
public class LogisticsController {

    private List<Delivery> deliveries;
    private List<Driver> drivers;

    // Separate counters to ensure unique IDs for each category
    private int nextDeliveryId = 1;
    private int nextDriverId = 1;

    public LogisticsController() {
        this.deliveries = new ArrayList<>();
        this.drivers = new ArrayList<>();
    }

    // --- DELIVERY METHODS ---

    public void addDelivery(String address, double weight) {
        deliveries.add(new Delivery(nextDeliveryId++, address, weight));
    }

    public List<Delivery> getAllDeliveries() {
        return deliveries;
    }

    public void removeDelivery(int index) {
        if (index >= 0 && index < deliveries.size()) {
            deliveries.remove(index);
        }
    }

    // --- DRIVER METHODS ---

    public void addDriver(String name, String vehicle) {
        drivers.add(new Driver(nextDriverId++, name, vehicle));
    }

    public List<Driver> getAllDrivers() {
        return drivers;
    }

    public void removeDriver(int index) {
        if (index >= 0 && index < drivers.size()) {
            drivers.remove(index);
        }
    }

    // --- PERSISTENCE (CSV) ---

    /**
     * Saves both lists to their respective CSV files.
     */
    public void saveAllData(String deliveryFile, String driverFile) {
        // Save Deliveries
        try (PrintWriter writer = new PrintWriter(new FileWriter(deliveryFile))) {
            for (Delivery d : deliveries) {
                writer.println(d.getId() + ";" + d.getDestinationAddress() + ";" + d.getWeight() + ";" + d.isDelivered());
            }
        } catch (IOException e) {
            System.err.println("Error saving deliveries: " + e.getMessage());
        }

        // Save Drivers
        try (PrintWriter writer = new PrintWriter(new FileWriter(driverFile))) {
            for (Driver d : drivers) {
                writer.println(d.getId() + ";" + d.getName() + ";" + d.getVehicle());
            }
        } catch (IOException e) {
            System.err.println("Error saving drivers: " + e.getMessage());
        }
    }

    /**
     * Loads both lists and restores the correct nextId state.
     */
    public void loadAllData(String deliveryFile, String driverFile) {
        loadDeliveries(deliveryFile);
        loadDrivers(driverFile);
    }

    private void loadDeliveries(String filename) {
        File file = new File(filename);
        if (!file.exists()) return;
        int maxId = 0;
        try (Scanner sc = new Scanner(file)) {
            deliveries.clear();
            while (sc.hasNextLine()) {
                String[] p = sc.nextLine().split(";");
                if (p.length == 4) {
                    int id = Integer.parseInt(p[0]);
                    Delivery d = new Delivery(id, p[1], Double.parseDouble(p[2]));
                    d.setDelivered(Boolean.parseBoolean(p[3]));
                    deliveries.add(d);
                    if (id > maxId) maxId = id;
                }
            }
            this.nextDeliveryId = maxId + 1;
        } catch (Exception e) { System.err.println("Load Error (Deliveries)"); }
    }

    private void loadDrivers(String filename) {
        File file = new File(filename);
        if (!file.exists()) return;
        int maxId = 0;
        try (Scanner sc = new Scanner(file)) {
            drivers.clear();
            while (sc.hasNextLine()) {
                String[] p = sc.nextLine().split(";");
                if (p.length == 3) {
                    int id = Integer.parseInt(p[0]);
                    drivers.add(new Driver(id, p[1], p[2]));
                    if (id > maxId) maxId = id;
                }
            }
            this.nextDriverId = maxId + 1;
        } catch (Exception e) { System.err.println("Load Error (Drivers)"); }
    }
}