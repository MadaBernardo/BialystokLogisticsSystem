# Bialystok Smart-Logistics System (TUI)

A professional terminal-based logistics management system developed for the **Human-Computer Interaction (HCI)** course. This application manages delivery tasks in the Bialystok region using a robust MVC architecture and high-visibility Text User Interface (TUI).

## 🚀 Key Features

- **Full CRUD Support**: Create, Read, Update, and Delete deliveries in real-time.
- **Persistent Storage**: Automatic data handling using CSV files (`deliveries.csv`).
- **Interactive TUI**: High-contrast interface with keyboard navigation and real-time feedback.
- **Robust ID Management**: Unique identity tracking to ensure data integrity.
- **Unit Tested**: Core logic validated with JUnit 5 to ensure reliability.

## 🏗️ Architecture

The project follows the **Model-View-Controller (MVC)** design pattern:

- **Model (`Delivery`)**: Represents the data structure and business logic of a delivery task.
- **View (`TuiView`)**: Handles the visual representation using the **Lanterna** library, focusing on UX and accessibility.
- **Controller (`LogisticsController`)**: Acts as the brain, managing data flow and file persistence.

## 🕹️ Controls & Navigation

The interface is fully keyboard-driven for maximum efficiency:

| Key | Action |
| :--- | :--- |
| `↑` / `↓` | Navigate through the delivery list (Highlight) |
| `N` | Add a New Delivery (Prompts for Address and Weight) |
| `D` | Toggle Status between **PENDING** and **DONE** |
| `X` | Delete the currently selected delivery |
| `Q` / `ESC`| Save all data and Quit the application |

## 🛠️ Technology Stack

- **Language**: Java 17+
- **Terminal UI**: [Lanterna](https://github.com/mabe02/lanterna)
- **Testing**: JUnit 5
- **Build Tool**: Maven

## 🧪 Running Tests

To run the automated unit tests and verify the logic integrity, use the following command (or run through your IDE):

```bash
mvn test