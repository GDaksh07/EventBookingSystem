package Managers;

// Imports the Event class
import Event_Management.Event;
import Event_Management.Concert;
import Event_Management.Workshop;
import Event_Management.Seminar;
import enums.EventStatus;

import java.util.ArrayList;
import java.util.Scanner;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class EventManager {

    private static final DateTimeFormatter INPUT_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    // Since main is static all other functions have to be static
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ArrayList<Event> events = new ArrayList<>();

        int choice;
        do {
            printMenu();
            choice = readInt(scanner, "Enter choice: ");

            // Case switch decides what method to use
            switch (choice) {
                case 1 -> addEvent(scanner, events);
                case 2 -> viewEventDetails(scanner, events);
                case 3 -> listAllEvents(events);
                case 4 -> cancelEvent(scanner, events);
                case 5 -> removeEvent(scanner, events);
                case 6 -> System.out.println("Exiting program...");
                default -> System.out.println("Invalid choice.");
            }
        } while (choice != 6);

        scanner.close();
    }

    // Prints the menu for choosing which part of the event class the user wants to view
    private static void printMenu() {
        System.out.println("\n=== EVENT MANAGEMENT MENU ===");
        System.out.println("1. Add Event");
        System.out.println("2. View Event Details");
        System.out.println("3. List All Events");
        System.out.println("4. Cancel Event");
        System.out.println("5. Remove Event");
        System.out.println("6. Exit");
    }

    // Main Actions the user can take
    // Adds an event
    private static void addEvent(Scanner scanner, ArrayList<Event> events) {
        // choice for which subclass to be in
        System.out.println("\nAdd which type?");
        System.out.println("1. Concert");
        System.out.println("2. Seminar");
        System.out.println("3. Workshop");

        int typeChoice = readInt(scanner, "Enter choice: ");

        String title = readNonEmpty(scanner, "Enter title: ");
        LocalDateTime dateTime = readDateTime(scanner);
        String location = readNonEmpty(scanner, "Enter location: ");
        int capacity = readPositiveInt(scanner, "Enter capacity (>0): ");

        // Chooses the specific type of event
        try {
            Event created;
            switch (typeChoice) {
                // Concert class
                case 1 -> {
                    int ageRestriction = readNonNegativeInt(scanner, "Enter age restriction (>=0): ");
                    created = new Concert(title, dateTime, location, capacity, ageRestriction);
                }
                // Seminar class
                case 2 -> {
                    String speaker = readNonEmpty(scanner, "Enter speaker name: ");
                    created = new Seminar(title, dateTime, location, capacity, speaker);
                }
                // Topic class
                case 3 -> {
                    String topic = readNonEmpty(scanner, "Enter workshop topic: ");
                    created = new Workshop(title, dateTime, location, capacity, topic);
                }
                default -> {
                    System.out.println("Invalid event type. Cancelled add.");
                    return;
                }
            }

            events.add(created);
            System.out.println("\nEvent added successfully!");
            System.out.println("Unique Event ID: " + created.getEventId());
        } catch (IllegalArgumentException e) {
            System.out.println("Could not create event: " + e.getMessage());
        }
    }

    // Cancels an event
    private static void cancelEvent(Scanner scanner, ArrayList<Event> events) {
        // Checks to see if there are any events listed
        if (events.isEmpty()) {
            System.out.println("No events available.");
            return;
        }

        // Checks which event the user wants to cancel
        String id = readNonEmpty(scanner, "Enter Event ID to cancel: ");
        Event e = findById(events, id);

        // Checks if the event exists
        if (e == null) {
            System.out.println("Event not found.");
            return;
        }

        // Checks to see if the event is already cancelled
        if (e.getStatus() == EventStatus.CANCELLED) {
            System.out.println("Event is already cancelled.");
            return;
        }

        // Cancels the event
        e.cancelEvent();
        System.out.println("Event cancelled: " + e.getEventId());
    }

    // Allows the user to view event details of specific events
    private static void viewEventDetails(Scanner scanner, ArrayList<Event> events) {
        // Checks to see if there are any events listed
        if (events.isEmpty()) {
            System.out.println("No events available.");
            return;
        }

        // Prompts user for an event to view
        String id = readNonEmpty(scanner, "Enter Event ID to search: ");
        Event e = findById(events, id);

        // Checks if the event exists
        if (e == null) {
            System.out.println("Event not found.");
            return;
        }

        // Displays event details
        System.out.println("\nEVENT DETAILS");
        // Automatically calls the toString method
        System.out.println(e);
    }

    // Lists all the events in the array list
    private static void listAllEvents(ArrayList<Event> events) {
        // Checks to see if there are any events listed
        if (events.isEmpty()) {
            System.out.println("No events available.");
            return;
        }

        /* Since event.toString() would put it in multiple lines
        this puts it in a single line with a list of all the events in the array
        much neater and cleaner
        The way the print format is done is in a way where it can show all the
        events in its designated "box" / space
        */
        System.out.printf("%-10s %-22s %-18s %-18s %-10s%n", "EventID", "Title", "DateTime", "Location", "Status");
        for (Event e : events) {
            System.out.printf("%-10s %-22s %-18s %-18s %-10s%n",
                    e.getEventId(),
                    shorten(e.getTitle(), 22),
                    e.getDateTime().format(INPUT_FMT),
                    shorten(e.getLocation(), 18),
                    e.getStatus());
        }
    }

    // Wipes the event the user wants to remove
    private static void removeEvent(Scanner scanner, ArrayList<Event> events) {
        // Checks to see if there are any events listed
        if (events.isEmpty()) {
            System.out.println("No events available.");
            return;
        }

        // Prompts user on which event to remove
        String id = readNonEmpty(scanner, "Enter Event ID to remove: ");
        for (int i = 0; i < events.size(); i++) {
            // Checks the whole array list to remove the event needed
            if (events.get(i).getEventId().equalsIgnoreCase(id)) {
                events.remove(i);
                System.out.println("Event removed.");
                return;
            }
        }
        System.out.println("Event not found."); // if the event id is not found
    }

    // Helper Methods
    // Checks if the event id is equal to any event inside the array list
    private static Event findById(ArrayList<Event> events, String id) {
        for (Event e : events) {
            if (e.getEventId().equalsIgnoreCase(id)) return e;
        }
        return null;
    }

    // Reads the local date time and formats it correctly
    private static LocalDateTime readDateTime(Scanner scanner) {
        while (true) {
            System.out.print("Enter date/time (yyyy-MM-dd HH:mm): ");
            String input = scanner.nextLine().trim();
            try {
                return LocalDateTime.parse(input, INPUT_FMT);
            } catch (DateTimeParseException ex) {
                System.out.println("Invalid format. Example: 2026-03-10 18:30");
            }
        }
    }

    // Scans for String input and makes sure they have an input and not empty
    private static String readNonEmpty(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = scanner.nextLine();
            if (s != null && !s.trim().isEmpty()) return s.trim();
            System.out.println("Input cannot be empty.");
        }
    }

    // Scans for an integer input and makes sure it is an integer and not a String
    // before spitting out an error
    private static int readInt(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = scanner.nextLine().trim();
            try {
                return Integer.parseInt(s);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid integer.");
            }
        }
    }

    // Makes sure the integer is not below 0
    private static int readPositiveInt(Scanner scanner, String prompt) {
        while (true) {
            int x = readInt(scanner, prompt);
            if (x > 0) return x;
            System.out.println("Must be > 0.");
        }
    }

    // Makes sure the integer is not below or equal to 0
    private static int readNonNegativeInt(Scanner scanner, String prompt) {
        while (true) {
            int x = readInt(scanner, prompt);
            if (x >= 0) return x;
            System.out.println("Must be >= 0.");
        }
    }

    // Shortens the length of the String for displaying it within the bounds set above
    private static String shorten(String s, int maxLen) {
        if (s == null) return "";
        if (s.length() <= maxLen) return s;
        return s.substring(0, Math.max(0, maxLen - 3)) + "...";
    }
}
