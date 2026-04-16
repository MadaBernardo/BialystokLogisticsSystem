package com.logistics.view;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import com.logistics.controller.LogisticsController;
import com.logistics.model.Delivery;

import java.io.IOException;
import java.util.List;

/**
 * View layer for Project 1.
 * Implements high-visibility UI and interactive delivery management.
 */
public class TuiView {
    private Screen screen;
    private String inputBuffer = "";
    private int selectedIndex = 0;

    public void start() throws IOException {
        Terminal terminal = new DefaultTerminalFactory().createTerminal();
        screen = new TerminalScreen(terminal);
        screen.startScreen();
    }

    /**
     * Main event loop for the interface.
     * Handles navigation, status toggling, and input triggers.
     */
    public void interactionLoop(LogisticsController controller) throws IOException {
        boolean keepRunning = true;

        while (keepRunning) {
            render(controller.getAllDeliveries());
            KeyStroke key = screen.readInput();

            // 1. EXIT LOGIC
            if (key.getKeyType() == KeyType.Escape || (key.getKeyType() == KeyType.Character && key.getCharacter() == 'q')) {
                keepRunning = false;
            }

            // 2. NAVIGATION LOGIC (Up/Down)
            else if (key.getKeyType() == KeyType.ArrowDown) {
                if (selectedIndex < controller.getAllDeliveries().size() - 1) {
                    selectedIndex++;
                }
            } else if (key.getKeyType() == KeyType.ArrowUp) {
                if (selectedIndex > 0) {
                    selectedIndex--;
                }
            }

            // 3. ACTION LOGIC: Toggle Status (Press 'D')
            else if (key.getKeyType() == KeyType.Character && key.getCharacter() == 'd') {
                List<Delivery> list = controller.getAllDeliveries();
                if (!list.isEmpty()) {
                    Delivery selected = list.get(selectedIndex);
                    selected.setDelivered(!selected.isDelivered());
                }
            }

            // 4. NEW DELIVERY LOGIC (Press 'N')
            else if (key.getKeyType() == KeyType.Character && key.getCharacter() == 'n') {
                handleNewDeliveryInput(controller);
            }

            else if (key.getKeyType() == KeyType.Character && key.getCharacter() == 'x') {
                List<Delivery> list = controller.getAllDeliveries();
                if (!list.isEmpty()) {
                    controller.removeDelivery(selectedIndex);

                    // Security setting: if you delete the last line, the cursor moves up one position.
                    if (selectedIndex >= controller.getAllDeliveries().size() && selectedIndex > 0) {
                        selectedIndex--;
                    }
                }
            }
        }

        screen.stopScreen(); // Cleanup after the loop ends
    }

    private void render(List<Delivery> deliveries) throws IOException {
        screen.clear();

        // Title and Legend
        drawText(5, 2, " === BIALYSTOK SMART-LOGISTICS SYSTEM === ", TextColor.ANSI.WHITE, TextColor.ANSI.BLUE, true);
        drawText(5, 3, " [N] Add | [D] Toggle Status | [X] Delete | [Q] Exit", TextColor.ANSI.CYAN, TextColor.ANSI.DEFAULT, false);

        // Table Header
        drawText(5, 5, String.format("%-4s | %-25s | %-7s | %s", "ID", "DESTINATION", "WEIGHT", "STATUS"), TextColor.ANSI.YELLOW, TextColor.ANSI.DEFAULT, true);
        drawText(5, 6, "------------------------------------------------------------", TextColor.ANSI.WHITE, TextColor.ANSI.DEFAULT, false);

        // Data Rows
        for (int i = 0; i < deliveries.size(); i++) {
            Delivery d = deliveries.get(i);
            boolean isSelected = (i == selectedIndex);

            TextColor backColor = isSelected ? TextColor.ANSI.BLUE_BRIGHT : TextColor.ANSI.DEFAULT;
            TextColor foreColor = isSelected ? TextColor.ANSI.BLACK : TextColor.ANSI.WHITE;
            TextColor statusColor = d.isDelivered() ? TextColor.ANSI.GREEN : TextColor.ANSI.RED;

            String row = String.format("%-4d | %-25s | %-7.1f | ", d.getId(), d.getDestinationAddress(), d.getWeight());

            drawText(5, 7 + i, row, foreColor, backColor, isSelected);
            drawText(48, 7 + i, d.isDelivered() ? "DONE   " : "PENDING", statusColor, backColor, true);
        }

        screen.refresh();
    }

    /**
     * Handles the multi-step input: first address, then weight.
     */
    private void handleNewDeliveryInput(LogisticsController controller) throws IOException {
        String tempAddress = "";
        String tempWeightStr = "";
        int step = 1; // 1 = Address, 2 = Weight
        boolean typing = true;

        while (typing) {
            // Render the current step
            String label = (step == 1) ? "ENTER DESTINATION" : "ENTER WEIGHT (kg)";
            String currentBuffer = (step == 1) ? tempAddress : tempWeightStr;
            renderInputBox(label, currentBuffer);

            KeyStroke key = screen.readInput();

            if (key.getKeyType() == KeyType.Enter) {
                if (step == 1) {
                    // VALIDATION: Cannot be empty
                    if (!tempAddress.trim().isEmpty()) {
                        step = 2; // Move to weight
                    }
                } else if (step == 2) {
                    // VALIDATION: Try to parse weight
                    try {
                        double weight = Double.parseDouble(tempWeightStr);
                        controller.addDelivery(tempAddress, weight);
                        typing = false; // Finished!
                    } catch (NumberFormatException e) {
                        tempWeightStr = ""; // Clear weight if it's not a number
                    }
                }
            } else if (key.getKeyType() == KeyType.Backspace) {
                if (step == 1 && tempAddress.length() > 0)
                    tempAddress = tempAddress.substring(0, tempAddress.length() - 1);
                else if (step == 2 && tempWeightStr.length() > 0)
                    tempWeightStr = tempWeightStr.substring(0, tempWeightStr.length() - 1);
            } else if (key.getKeyType() == KeyType.Character) {
                if (step == 1) tempAddress += key.getCharacter();
                else tempWeightStr += key.getCharacter();
            } else if (key.getKeyType() == KeyType.Escape) {
                typing = false; // Cancel everything
            }

            render(controller.getAllDeliveries()); // Keep table visible in background
        }
    }

    /**
     * Updated to show a dynamic label.
     */
    private void renderInputBox(String label, String buffer) throws IOException {
        drawText(5, 18, label + ": " + buffer + "_", TextColor.ANSI.YELLOW, TextColor.ANSI.DEFAULT, true);
        screen.refresh();
    }

    private void drawText(int col, int row, String text, TextColor fore, TextColor back, boolean bold) {
        var graphics = screen.newTextGraphics().setForegroundColor(fore).setBackgroundColor(back);
        if (bold) graphics.enableModifiers(SGR.BOLD);
        graphics.putString(col, row, text);
    }
}