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
import com.logistics.model.Driver;

import java.io.IOException;
import java.util.List;

public class TuiView {
    private Screen screen;
    private int selectedIndex = 0;
    private enum ViewMode { DELIVERIES, DRIVERS }
    private ViewMode currentMode = ViewMode.DELIVERIES;

    public void start() throws IOException {
        Terminal terminal = new DefaultTerminalFactory().createTerminal();
        screen = new TerminalScreen(terminal);
        screen.startScreen();
    }

    public void interactionLoop(LogisticsController controller) throws IOException {
        boolean keepRunning = true;
        while (keepRunning) {
            render(controller);
            KeyStroke key = screen.readInput();

            if (key.getKeyType() == KeyType.Escape || (key.getKeyType() == KeyType.Character && key.getCharacter() == 'q')) {
                keepRunning = false;
            } else if (key.getKeyType() == KeyType.Character && key.getCharacter() == 'm') {
                currentMode = (currentMode == ViewMode.DELIVERIES) ? ViewMode.DRIVERS : ViewMode.DELIVERIES;
                selectedIndex = 0;
            } else if (key.getKeyType() == KeyType.ArrowDown) {
                int max = (currentMode == ViewMode.DELIVERIES) ? controller.getAllDeliveries().size() : controller.getAllDrivers().size();
                if (selectedIndex < max - 1) selectedIndex++;
            } else if (key.getKeyType() == KeyType.ArrowUp) {
                if (selectedIndex > 0) selectedIndex--;
            } else if (key.getKeyType() == KeyType.Character && key.getCharacter() == 'n') {
                if (currentMode == ViewMode.DELIVERIES) handleNewDeliveryInput(controller);
                else handleNewDriverInput(controller);
            } else if (key.getKeyType() == KeyType.Character && key.getCharacter() == 'x') {
                if (currentMode == ViewMode.DELIVERIES) controller.removeDelivery(selectedIndex);
                else controller.removeDriver(selectedIndex);
                if (selectedIndex > 0) selectedIndex--;
            } else if (key.getKeyType() == KeyType.Character && key.getCharacter() == 'd' && currentMode == ViewMode.DELIVERIES) {
                List<Delivery> list = controller.getAllDeliveries();
                if (!list.isEmpty()) {
                    Delivery selected = list.get(selectedIndex);
                    selected.setDelivered(!selected.isDelivered());
                }
            }
        }
        screen.stopScreen();
    }

    private void render(LogisticsController controller) throws IOException {
        screen.clear();
        String modeName = (currentMode == ViewMode.DELIVERIES) ? "DELIVERIES" : "DRIVERS";
        drawText(5, 2, " === BIALYSTOK LOGISTICS [" + modeName + "] === ", TextColor.ANSI.WHITE, TextColor.ANSI.BLUE, true);

        // Menu legend fixed
        String menu = (currentMode == ViewMode.DELIVERIES)
                ? " [M] Mode | [N] Add | [D] Toggle | [X] Delete | [Q] Exit"
                : " [M] Mode | [N] Add | [X] Delete | [Q] Exit";
        drawText(5, 3, menu, TextColor.ANSI.CYAN, TextColor.ANSI.DEFAULT, false);

        if (currentMode == ViewMode.DELIVERIES) renderDeliveriesTable(controller.getAllDeliveries());
        else renderDriversTable(controller.getAllDrivers());
        screen.refresh();
    }

    private void renderDeliveriesTable(List<Delivery> list) throws IOException {
        drawText(5, 5, String.format("%-4s | %-25s | %-7s | %s", "ID", "DESTINATION", "WEIGHT", "STATUS"), TextColor.ANSI.YELLOW, TextColor.ANSI.DEFAULT, true);
        for (int i = 0; i < list.size(); i++) {
            Delivery d = list.get(i);
            boolean isSel = (i == selectedIndex);
            TextColor back = isSel ? TextColor.ANSI.BLUE_BRIGHT : TextColor.ANSI.DEFAULT;
            String row = String.format("%-4d | %-25s | %-7.1f | ", d.getId(), d.getDestinationAddress(), d.getWeight());
            drawText(5, 7 + i, row, isSel ? TextColor.ANSI.BLACK : TextColor.ANSI.WHITE, back, isSel);
            drawText(48, 7 + i, d.isDelivered() ? "DONE" : "PENDING", d.isDelivered() ? TextColor.ANSI.GREEN : TextColor.ANSI.RED, back, true);
        }
    }

    private void renderDriversTable(List<Driver> list) throws IOException {
        drawText(5, 5, String.format("%-4s | %-25s | %-15s", "ID", "NAME", "VEHICLE"), TextColor.ANSI.YELLOW, TextColor.ANSI.DEFAULT, true);
        for (int i = 0; i < list.size(); i++) {
            Driver d = list.get(i);
            boolean isSel = (i == selectedIndex);
            // FIXED: Using getName() instead of getDestinationAddress()
            String row = String.format("%-4d | %-25s | %-15s", d.getId(), d.getName(), d.getVehicle());
            drawText(5, 7 + i, row, isSel ? TextColor.ANSI.BLACK : TextColor.ANSI.WHITE, isSel ? TextColor.ANSI.GREEN_BRIGHT : TextColor.ANSI.DEFAULT, isSel);
        }
    }

    private void handleNewDeliveryInput(LogisticsController controller) throws IOException {
        String addr = genericInput("ENTER DESTINATION (ESC to cancel)");
        if (addr == null || addr.isEmpty()) return;
        String weightS = genericInput("ENTER WEIGHT (kg)");
        if (weightS == null || weightS.isEmpty()) return;
        try {
            controller.addDelivery(addr, Double.parseDouble(weightS));
        } catch (Exception e) {}
    }

    private void handleNewDriverInput(LogisticsController controller) throws IOException {
        String name = genericInput("ENTER DRIVER NAME (ESC to cancel)");
        if (name == null || name.isEmpty()) return;
        String vehicle = genericInput("ENTER VEHICLE TYPE");
        if (vehicle == null || vehicle.isEmpty()) return;
        controller.addDriver(name, vehicle);
    }

    private String genericInput(String label) throws IOException {
        String input = "";
        while (true) {
            // Clean the input line before drawing to prevent ghost text (Fix for Captura de ecrã 2026-05-02 132602.png)
            drawText(5, 20, " ".repeat(80), TextColor.ANSI.DEFAULT, TextColor.ANSI.DEFAULT, false);
            drawText(5, 20, label + ": " + input + "_", TextColor.ANSI.YELLOW, TextColor.ANSI.DEFAULT, true);
            screen.refresh();

            KeyStroke k = screen.readInput();
            if (k.getKeyType() == KeyType.Enter) return input;
            if (k.getKeyType() == KeyType.Escape) return null; // Allows canceling
            if (k.getKeyType() == KeyType.Backspace && input.length() > 0) input = input.substring(0, input.length() - 1);
            if (k.getKeyType() == KeyType.Character) input += k.getCharacter();
        }
    }

    private void drawText(int col, int row, String text, TextColor fore, TextColor back, boolean bold) {
        var g = screen.newTextGraphics().setForegroundColor(fore).setBackgroundColor(back);
        if (bold) g.enableModifiers(SGR.BOLD);
        g.putString(col, row, text);
    }
}