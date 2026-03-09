package Waitlist_Management;

import Event_Management.Event;
import User_Management.User;

import java.util.List;
import java.util.Scanner;

// Console interface for waitlist operations.
// This class does not contain booking logic.
// It only calls WaitlistManager and displays results.
public class WaitlistUI {

    private final WaitlistManager waitlistManager; // logic layer
    private final Scanner scanner = new Scanner(System.in); // input reader

    public WaitlistUI(WaitlistManager waitlistManager) {
        this.waitlistManager = waitlistManager;
    }

    // Main waitlist menu loop
    public void showMenu(List<Event> events, List<User> users) {

        while (true) {

            System.out.println("\n=== Waitlist Management ===");
            System.out.println("1) View Event Waitlist");
            System.out.println("2) Remove Waitlisted User");
            System.out.println("3) Cancel Confirmed -> Show Promotion");
            System.out.println("0) Back");

            System.out.print("Choose: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> viewEventWaitlist(events);
                case "2" -> removeWaitlistedUser(events, users);
                case "3" -> cancelConfirmedAndShowPromotion(events, users);
                case "0" -> { return; }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    // Displays waitlist for selected event
    private void viewEventWaitlist(List<Event> events) {

        Event event = pickEvent(events);
        if (event == null) return;

        List<User> waitlist = waitlistManager.viewWaitlist(event);

        if (waitlist.isEmpty()) {
            System.out.println("Waitlist is empty.");
            return;
        }

        System.out.println("\nWaitlist for: " + event.getTitle());

        for (int i = 0; i < waitlist.size(); i++) {
            User u = waitlist.get(i);
            System.out.println((i + 1) + ") " + u.getName());
        }
    }

    // Removes user from waitlist
    private void removeWaitlistedUser(List<Event> events, List<User> users) {

        Event event = pickEvent(events);
        if (event == null) return;

        User user = pickUser(users);
        if (user == null) return;

        try {
            waitlistManager.removeFromWaitlist(event, user);
            System.out.println("User removed from waitlist.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // Cancels confirmed booking and prints promotion result
    private void cancelConfirmedAndShowPromotion(List<Event> events, List<User> users) {

        Event event = pickEvent(events);
        if (event == null) return;

        User user = pickUser(users);
        if (user == null) return;

        try {
            PromotionResult result =
                    waitlistManager.cancelConfirmedWithResult(event, user);

            System.out.println("Confirmed booking cancelled.");

            if (result.isPromoted()) {
                System.out.println("PROMOTION: "
                        + result.getPromotedUser().getName()
                        + " moved from waitlist to confirmed.");
            } else {
                System.out.println("No promotion happened.");
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // Lets user select an event from list
    private Event pickEvent(List<Event> events) {

        if (events == null || events.isEmpty()) {
            System.out.println("No events available.");
            return null;
        }

        for (int i = 0; i < events.size(); i++) {
            System.out.println((i + 1) + ") " + events.get(i).getTitle());
        }

        System.out.print("Enter number: ");
        String input = scanner.nextLine().trim();

        try {
            int idx = Integer.parseInt(input) - 1;
            if (idx < 0 || idx >= events.size()) return null;
            return events.get(idx);
        } catch (Exception e) {
            return null;
        }
    }

    // Lets user select a user from list
    private User pickUser(List<User> users) {

        if (users == null || users.isEmpty()) {
            System.out.println("No users available.");
            return null;
        }

        for (int i = 0; i < users.size(); i++) {
            System.out.println((i + 1) + ") " + users.get(i).getName());
        }

        System.out.print("Enter number: ");
        String input = scanner.nextLine().trim();

        try {
            int idx = Integer.parseInt(input) - 1;
            if (idx < 0 || idx >= users.size()) return null;
            return users.get(idx);
        } catch (Exception e) {
            return null;
        }
    }
}