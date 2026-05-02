package com.logistics;

import com.logistics.controller.LogisticsController;
import com.logistics.view.TuiView;
import java.io.IOException;

public class Main {
    private static final String DELIVERY_FILE = "deliveries.csv";
    private static final String DRIVER_FILE = "drivers.csv";

    public static void main(String[] args) {
        LogisticsController controller = new LogisticsController();
        TuiView view = new TuiView();

        // 1. LOAD DATA
        controller.loadAllData(DELIVERY_FILE, DRIVER_FILE);

        // 2. SHUTDOWN HOOK (The Safety Net)
        // This runs even if you click the [X] or if the program crashes
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\n[SYSTEM] Emergency save triggered...");
            controller.saveAllData(DELIVERY_FILE, DRIVER_FILE);
        }));

        if (controller.getAllDeliveries().isEmpty()) {
            controller.addDelivery("Bialystok Central Station", 50.0);
        }

        try {
            view.start();
            view.interactionLoop(controller);
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        } finally {
            // Normal exit via [Q]
            System.out.println("Closing system normally...");
            controller.saveAllData(DELIVERY_FILE, DRIVER_FILE);
        }
    }
}