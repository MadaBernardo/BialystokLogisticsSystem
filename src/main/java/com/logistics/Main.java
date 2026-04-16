package com.logistics;

import com.logistics.controller.LogisticsController;
import com.logistics.view.TuiView;
import java.io.IOException;

/**
 * Main entry point for the logistics system.
 * Handles the application lifecycle: Load -> Run -> Save.
 */
public class Main {
    public static void main(String[] args) {
        // 1. Initialize Controller and View
        LogisticsController controller = new LogisticsController();
        TuiView view = new TuiView();

        // 2. LOAD: Try to recover previous data from file
        // This must happen BEFORE the UI starts
        controller.loadFromFile("deliveries.csv");

        // 3. Optional: Add a default delivery if the list is empty (First time use)
        if (controller.getAllDeliveries().isEmpty()) {
            controller.addDelivery("Bialystok Central Station", 50.0);
        }

        try {
            // 4. START: Launch the terminal interface
            view.start();

            // 5. RUN: Enter the interaction loop (This blocks until user quits)
            view.interactionLoop(controller);

        } catch (IOException e) {
            System.err.println("CRITICAL ERROR: " + e.getMessage());
        } finally {
            // 6. SAVE: Store data when the program ends
            // The 'finally' block ensures it saves even if a small error occurs
            System.out.println("Saving data to deliveries.csv...");
            controller.saveToFile("deliveries.csv");
        }
    }
}