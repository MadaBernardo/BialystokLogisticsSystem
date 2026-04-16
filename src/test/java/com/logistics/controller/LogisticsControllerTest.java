package com.logistics.controller;

import com.logistics.model.Delivery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for LogisticsController.
 * Follows the AAA (Arrange, Act, Assert) pattern.
 */
class LogisticsControllerTest {

    private LogisticsController controller;

    @BeforeEach
    /**
     * Sets up a fresh controller before each individual test.
     * This ensures tests don't interfere with each other.
     */
    void setUp() {
        controller = new LogisticsController();
    }

    @Test
    @DisplayName("Should add a delivery and increase the list size")
    void shouldAddDeliveryCorrectly() {
        // 1. ARRANGE
        String address = "Ulica Lipowa 10, Bialystok";
        double weight = 15.5;

        // 2. ACT
        controller.addDelivery(address, weight);

        // 3. ASSERT
        List<Delivery> deliveries = controller.getAllDeliveries();
        assertEquals(1, deliveries.size(), "The list size should be 1 after adding a delivery.");
        assertEquals(address, deliveries.get(0).getDestinationAddress(), "The address should match.");
        assertEquals(weight, deliveries.get(0).getWeight(), "The weight should match.");
    }

    @Test
    @DisplayName("Should remove a delivery by its index")
    void shouldRemoveDeliveryCorrectly() {
        // 1. ARRANGE: Add two deliveries to have something to remove
        controller.addDelivery("Address 1", 5.0);
        controller.addDelivery("Address 2", 10.0);

        // 2. ACT: Remove the first one
        controller.removeDelivery(0);

        // 3. ASSERT
        List<Delivery> deliveries = controller.getAllDeliveries();
        assertEquals(1, deliveries.size(), "The list size should decrease to 1.");
        assertEquals("Address 2", deliveries.get(0).getDestinationAddress(), "The remaining delivery should be 'Address 2'.");
    }

    @Test
    @DisplayName("Should maintain ID sequence correctly")
    void shouldIncrementIdsCorrectly() {
        // 1. ARRANGE & ACT
        controller.addDelivery("First", 1.0);
        controller.addDelivery("Second", 2.0);

        // 2. ASSERT
        List<Delivery> deliveries = controller.getAllDeliveries();
        assertEquals(1, deliveries.get(0).getId());
        assertEquals(2, deliveries.get(1).getId());
    }

    @Test
    @DisplayName("Should NOT reuse IDs after a delivery is deleted (Unique Identity)")
    void shouldMaintainUniqueIdAfterDeletion() {
        // 1. ARRANGE
        controller.addDelivery("Street A", 10.0); // Recebe ID 1
        controller.addDelivery("Street B", 20.0); // Recebe ID 2

        // 2. ACT
        controller.removeDelivery(0); // Removemos o ID 1. A lista agora só tem o ID 2.
        controller.addDelivery("Street C", 30.0); // O sistema deve dar o ID 3 (e não o 2 novamente)

        // 3. ASSERT
        List<Delivery> list = controller.getAllDeliveries();
        assertEquals(2, list.size());
        // O último item adicionado tem de ter o ID 3
        assertEquals(3, list.get(1).getId(), "New delivery should have ID 3 even after deleting ID 1");
    }

}