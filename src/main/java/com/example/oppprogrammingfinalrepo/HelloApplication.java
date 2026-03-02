package com.example.oppprogrammingfinalrepo;

import Booking_Management.Booking;
import Event_Management.Concert;
import Event_Management.Event;
import Event_Management.Seminar;
import Event_Management.Workshop;
import User_Management.User;
import enums.BookingStatus;
import enums.EventType;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class HelloApplication extends Application {

    // Shared in-memory state (single source of truth)
    private final List<Event> events = new ArrayList<>();
    private final List<User> users = new ArrayList<>();
    private final Map<String, Booking> bookings = new HashMap<>();

    // Date/time format used for parsing and display
    private static final DateTimeFormatter INPUT_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final DateTimeFormatter DISPLAY_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    // UI controls referenced across methods
    // Displays the designated output
    private TextArea eventOutput;
    private TextArea userOutput;
    private TextArea bookingOutput;

    // User selects for booking
    private ComboBox<String> bookingUserCombo;
    private ComboBox<String> bookingEventCombo;

    @Override
    public void start(Stage stage) {

        // Main Menu
        Label title = new Label("OPP Final Project - Main Menu");

        // Creates buttons for each topic
        Button eventBtn = new Button("Event Management");
        Button userBtn = new Button("User Management");
        Button waitlistBtn = new Button("Waitlist (View Only)");
        Button bookingBtn = new Button("Booking Management");

        // Sets the width of the button
        eventBtn.setPrefWidth(200);
        userBtn.setPrefWidth(200);
        waitlistBtn.setPrefWidth(200);
        bookingBtn.setPrefWidth(200);

        // Vertical layout for the main menu
        VBox mainRoot = new VBox(12, title, eventBtn, userBtn, waitlistBtn, bookingBtn);
        mainRoot.setPadding(new Insets(18));
        Scene mainScene = new Scene(mainRoot, 600, 420);

        // Event Screen
        eventOutput = new TextArea();
        eventOutput.setEditable(false); // prevents the user from editing the text
        eventOutput.setPrefHeight(160); // fixed display height

        // Text for the different boxes the user can input into
        TextField evTitle = new TextField();
        evTitle.setPromptText("Title");

        TextField evDate = new TextField();
        evDate.setPromptText("DateTime (yyyy-MM-dd HH:mm)");

        TextField evLocation = new TextField();
        evLocation.setPromptText("Location");

        TextField evCapacity = new TextField();
        evCapacity.setPromptText("Capacity (integer)");

        // Dropdown menu for selecting each subclass type
        ComboBox<EventType> evType = new ComboBox<>();
        evType.setItems(FXCollections.observableArrayList(EventType.CONCERT, EventType.SEMINAR, EventType.WORKSHOP));
        evType.setValue(EventType.CONCERT); // Sets the dropdown first to CONCERT

        // Extra fields used for subclass constructors
        TextField extraField = new TextField();
        extraField.setPromptText("Extra (age/topic/materials)");

        Button createEventBtn = new Button("Create Event");
        Button refreshEventsBtn = new Button("Refresh List");
        TextField cancelEventId = new TextField();
        cancelEventId.setPromptText("Event ID to cancel");
        Button cancelEventBtn = new Button("Cancel Event");
        Button backFromEvent = new Button("Back");

        HBox evFormRow1 = new HBox(8, evTitle, evDate, evLocation);
        HBox evFormRow2 = new HBox(8, evCapacity, evType, extraField);
        HBox evFormRow3 = new HBox(8, createEventBtn, refreshEventsBtn, cancelEventId, cancelEventBtn);
        VBox eventRoot = new VBox(10, new Label("Event Management"), eventOutput, evFormRow1, evFormRow2, evFormRow3, backFromEvent);
        eventRoot.setPadding(new Insets(12));
        Scene eventScene = new Scene(eventRoot, 800, 500);

        // User Screen
        // Same idea as the Event Screen with all the textboxes
        // Only difference is it's stored in a different location
        userOutput = new TextArea();
        userOutput.setEditable(false);
        userOutput.setPrefHeight(200);

        TextField userName = new TextField();
        userName.setPromptText("Given name");

        TextField userSurname = new TextField();
        userSurname.setPromptText("Surname");

        TextField userMonth = new TextField();
        userMonth.setPromptText("MM");

        TextField userDay = new TextField();
        userDay.setPromptText("DD");

        TextField userYear = new TextField();
        userYear.setPromptText("YYYY");

        TextField userIdField = new TextField();
        userIdField.setPromptText("ID (integer, <=999999)");

        Button addUserBtn = new Button("Add User");
        Button refreshUsersBtn = new Button("Refresh Users");
        TextField removeUserId = new TextField();
        removeUserId.setPromptText("User ID to remove");
        Button removeUserBtn = new Button("Remove User");
        Button backFromUser = new Button("Back");

        // inputs for each container
        HBox userFormRow = new HBox(8, userName, userSurname, userMonth, userDay, userYear, userIdField);
        HBox userFormRow2 = new HBox(8, addUserBtn, refreshUsersBtn, removeUserId, removeUserBtn);
        // This stacks both layers as an input from both horizontal layers
        VBox userRoot = new VBox(10, new Label("User Management"), userOutput, userFormRow, userFormRow2, backFromUser);
        // Padding - 12 pixels of whitespace on all sides
        userRoot.setPadding(new Insets(12));
        Scene userScene = new Scene(userRoot, 900, 520);

        // Waitlist Viewer (read only)
        TextArea waitlistOutput = new TextArea();
        waitlistOutput.setEditable(false);
        waitlistOutput.setPrefHeight(400);
        Button backFromWaitlist = new Button("Back");
        VBox waitRoot = new VBox(10, new Label("Waitlist View (per event)"), waitlistOutput, backFromWaitlist);
        waitRoot.setPadding(new Insets(12));
        Scene waitScene = new Scene(waitRoot, 700, 500);

        // Booking Screen
        // Same idea as Event Screen with textboxes
        bookingOutput = new TextArea();
        bookingOutput.setEditable(false);
        bookingOutput.setPrefHeight(200);

        bookingUserCombo = new ComboBox<>();
        bookingUserCombo.setPromptText("Select user (ID - name)");

        bookingEventCombo = new ComboBox<>();
        bookingEventCombo.setPromptText("Select event (ID - title)");

        TextField bookingIdField = new TextField();
        bookingIdField.setPromptText("Booking ID (unique)");

        Button createBookingBtn = new Button("Create Booking");
        Button refreshBookingsBtn = new Button("Refresh Bookings");
        TextField cancelBookingId = new TextField();
        cancelBookingId.setPromptText("Booking ID to cancel");
        Button cancelBookingBtn = new Button("Cancel Booking");
        Button backFromBooking = new Button("Back");

        HBox bookingRow1 = new HBox(8, bookingUserCombo, bookingEventCombo, bookingIdField);
        HBox bookingRow2 = new HBox(8, createBookingBtn, refreshBookingsBtn, cancelBookingId, cancelBookingBtn);
        VBox bookingRoot = new VBox(10, new Label("Booking Management"), bookingOutput, bookingRow1, bookingRow2, backFromBooking);
        bookingRoot.setPadding(new Insets(12));
        Scene bookingScene = new Scene(bookingRoot, 900, 520);

        // Wire Navigation
        eventBtn.setOnAction(e -> {
            refreshEvents(); // Ensures list is updated
            stage.setScene(eventScene); // Switches scene
        });
        userBtn.setOnAction(e -> {
            refreshUsers();
            stage.setScene(userScene);
        });
        waitlistBtn.setOnAction(e -> {
            refreshWaitlist(waitlistOutput);
            stage.setScene(waitScene);
        });
        bookingBtn.setOnAction(e -> {
            refreshBookings();
            stage.setScene(bookingScene);
        });

        backFromEvent.setOnAction(e -> stage.setScene(mainScene));
        backFromUser.setOnAction(e -> stage.setScene(mainScene));
        backFromWaitlist.setOnAction(e -> stage.setScene(mainScene));
        backFromBooking.setOnAction(e -> stage.setScene(mainScene));

        // Actions: EVENTS
        createEventBtn.setOnAction(e -> {
            try {
                // Reads and validates user input
                String titleVal = evTitle.getText().trim();
                LocalDateTime dt = LocalDateTime.parse(evDate.getText().trim(), INPUT_FMT);
                String locVal = evLocation.getText().trim();
                int cap = Integer.parseInt(evCapacity.getText().trim());
                EventType t = evType.getValue();

                Event created;
                // Creates correct subclasses based on the type of event
                switch (t) {
                    case CONCERT -> {
                        int age = 0;
                        try { age = Integer.parseInt(extraField.getText().trim()); } catch (Exception ex) { age = 0; }
                        created = new Concert(titleVal, dt, locVal, cap, age);
                    }
                    case SEMINAR -> {
                        String topic = extraField.getText().trim().isEmpty() ? "General" : extraField.getText().trim();
                        created = new Seminar(titleVal, dt, locVal, cap, topic);
                    }
                    case WORKSHOP -> {
                        String materials = extraField.getText().trim().isEmpty() ? "N/A" : extraField.getText().trim();
                        created = new Workshop(titleVal, dt, locVal, cap, materials);
                    }
                    default -> throw new IllegalStateException("Unexpected event type");
                }
                created.setEventType(t);
                events.add(created);
                refreshEvents();
            } catch (Exception ex) {
                eventOutput.setText("CREATE ERROR: " + ex.getClass().getSimpleName() + " - " + ex.getMessage());
            }
        });

        refreshEventsBtn.setOnAction(e -> refreshEvents());

        cancelEventBtn.setOnAction(e -> {
            String id = cancelEventId.getText().trim();
            if (id.isEmpty()) {
                eventOutput.setText("Enter Event ID to cancel.");
                return;
            }
            Event found = findEventById(id);
            if (found == null) {
                eventOutput.setText("Event not found: " + id);
                return;
            }
            try {
                found.cancelEvent();
                // If cancelling, mark all confirmed bookings CANCELLED and promote waitlist
                List<Booking> toCancel = new ArrayList<>();
                for (Booking b : new ArrayList<>(found.getConfirmedBookings())) {
                    b.setStatus(BookingStatus.CANCELLED);
                    toCancel.add(b);
                }
                found.getConfirmedBookings().clear();
                refreshEvents();
            } catch (Exception ex) {
                eventOutput.setText("CANCEL ERROR: " + ex.getMessage());
            }
        });

        // Actions: USERS
        // Relatively similar to the events action
        addUserBtn.setOnAction(e -> {
            try {
                String n = userName.getText().trim();
                String s = userSurname.getText().trim();
                int m = Integer.parseInt(userMonth.getText().trim());
                int d = Integer.parseInt(userDay.getText().trim());
                int y = Integer.parseInt(userYear.getText().trim());
                int id = Integer.parseInt(userIdField.getText().trim());

                User u = new User(n, s, m, d, y, id); // defaults to STUDENT
                // Still need to add STAFF and GUEST
                users.add(u);
                refreshUsers();
            } catch (Exception ex) {
                userOutput.setText("ADD USER ERROR: " + ex.getClass().getSimpleName() + " - " + ex.getMessage());
            }
        });

        refreshUsersBtn.setOnAction(e -> refreshUsers());

        removeUserBtn.setOnAction(e -> {
            try {
                int id = Integer.parseInt(removeUserId.getText().trim());
                User found = findUserById(id);
                if (found == null) {
                    userOutput.setText("User not found: " + id);
                    return;
                }
                users.remove(found);
                // Also remove bookings belonging to this user
                bookings.values().removeIf(b -> b.getUser().equals(found));
                refreshUsers();
            } catch (Exception ex) {
                userOutput.setText("REMOVE USER ERROR: " + ex.getMessage());
            }
        });

        // Actions: BOOKINGS
        // Same thing as before again but instead utilizes the users and events
        // created to book events
        createBookingBtn.setOnAction(e -> {
            try {
                String bookingId = bookingIdField.getText().trim();
                String userEntry = bookingUserCombo.getValue();
                String eventEntry = bookingEventCombo.getValue();

                if (bookingId.isEmpty() || userEntry == null || eventEntry == null) {
                    bookingOutput.setText("Please supply booking ID, user, and event.");
                    return;
                }

                int userId = Integer.parseInt(userEntry.split(" - ")[0]);
                String eventId = eventEntry.split(" - ")[0];

                User u = findUserById(userId);
                Event ev = findEventById(eventId);

                if (u == null || ev == null) {
                    bookingOutput.setText("User or event not found.");
                    return;
                }

                // Event must be ACTIVE
                if (ev.getStatus() != enums.EventStatus.ACTIVE) {
                    bookingOutput.setText("Event is not active.");
                    return;
                }

                // Prevent duplicate (same user + event, not cancelled)
                for (Booking b : bookings.values()) {
                    if (b.getUser().equals(u) && b.getEvent().equals(ev) && b.getStatus() != BookingStatus.CANCELLED) {
                        bookingOutput.setText("User already booked this event.");
                        return;
                    }
                }

                Booking newBooking;
                if (ev.hasCapacity()) {
                    newBooking = new Booking(bookingId, u, ev, BookingStatus.CONFIRMED);
                    ev.addConfirmedBooking(newBooking);
                } else {
                    newBooking = new Booking(bookingId, u, ev, BookingStatus.WAITLISTED);
                    ev.addToWaitlist(newBooking);
                }

                bookings.put(bookingId, newBooking);
                refreshBookings();
                refreshEvents(); // reflect capacity change
            } catch (Exception ex) {
                bookingOutput.setText("CREATE BOOKING ERROR: " + ex.getClass().getSimpleName() + " - " + ex.getMessage());
            }
        });

        refreshBookingsBtn.setOnAction(e -> refreshBookings());

        cancelBookingBtn.setOnAction(e -> {
            String id = cancelBookingId.getText().trim();
            if (id.isEmpty()) {
                bookingOutput.setText("Enter booking ID to cancel.");
                return;
            }
            Booking b = bookings.get(id);
            if (b == null) {
                bookingOutput.setText("Booking not found: " + id);
                return;
            }
            if (b.getStatus() == BookingStatus.CANCELLED) {
                bookingOutput.setText("Already cancelled.");
                return;
            }

            Event ev = b.getEvent();
            boolean wasConfirmed = b.getStatus() == BookingStatus.CONFIRMED;

            b.setStatus(BookingStatus.CANCELLED);
            bookings.remove(id);

            if (wasConfirmed) {
                ev.removeConfirmedBooking(b);
                // promote next waitlist booking if exists
                Booking promoted = ev.pollWaitlist();
                if (promoted != null) {
                    promoted.setStatus(BookingStatus.CONFIRMED);
                    ev.addConfirmedBooking(promoted);
                }
            } else {
                ev.removeFromWaitlist(b);
            }

            refreshBookings();
            refreshEvents();
        });

        // Initial sample data
        seedSampleData();

        // Shows main screen
        stage.setTitle("OPP Final Project");
        stage.setScene(mainScene);
        stage.show();
    }

    // Helper methods

    private void refreshEvents() {
        // Outputs a refreshed version of the events screen
        StringBuilder sb = new StringBuilder();
        for (Event ev : events) {
            sb.append(ev.getEventId())
                    .append(" | ").append(ev.getTitle())
                    .append(" | ").append(ev.getDateTime().format(DISPLAY_FMT))
                    .append(" | ").append(ev.getLocation())
                    .append(" | ").append(ev.getStatus())
                    .append(" | Confirmed: ").append(ev.getConfirmedBookings().size())
                    .append(" | Waitlist: ").append(ev.getWaitlist().size())
                    .append("\n");
        }
        eventOutput.setText(sb.toString());
        // refresh booking/event selection comboboxes
        refreshBookingCombos();
    }

    private void refreshUsers() {
        // Outputs a refreshed version of the users screen
        StringBuilder sb = new StringBuilder();
        for (User u : users) {
            sb.append(u.getID())
                    .append(" - ").append(u.getName())
                    .append(" ").append(u.getSurname())
                    .append(" | ").append(u.getBirthdate())
                    .append("\n");
        }
        userOutput.setText(sb.toString());
        refreshBookingCombos();
    }

    private void refreshBookings() {
        // Outputs a refreshed version of the bookings screen
        StringBuilder sb = new StringBuilder();
        for (Booking b : bookings.values()) {
            sb.append(b.getBookingId())
                    .append(" | user=").append(b.getUser().getID())
                    .append(" | event=").append(b.getEvent().getEventId())
                    .append(" | status=").append(b.getStatus())
                    .append("\n");
        }
        bookingOutput.setText(sb.toString());
        refreshBookingCombos();
    }

    private void refreshWaitlist(TextArea waitlistOutput) {
        // Outputs a refreshed version of the waitlist screen
        StringBuilder sb = new StringBuilder();
        for (Event ev : events) {
            sb.append("Event ").append(ev.getEventId()).append(" - ").append(ev.getTitle()).append("\n");
            sb.append(" Waitlist:\n");
            for (Booking b : ev.getWaitlist()) {
                sb.append("  - ").append(b.getBookingId()).append(" | user=").append(b.getUser().getID()).append("\n");
            }
            sb.append("\n");
        }
        waitlistOutput.setText(sb.toString());
    }

    // Updates the dropdown menus with current users and events
    private void refreshBookingCombos() {
        // Populate combos with "id - display" strings
        List<String> uList = new ArrayList<>();
        for (User u : users) {
            uList.add(u.getID() + " - " + u.getName());
        }
        // Replace the items inside the user comboBox
        bookingUserCombo.setItems(FXCollections.observableArrayList(uList));

        List<String> evList = new ArrayList<>();
        for (Event ev : events) {
            evList.add(ev.getEventId() + " - " + ev.getTitle());
        }
        bookingEventCombo.setItems(FXCollections.observableArrayList(evList));
    }

    // Finds user by id (linear search)
    private User findUserById(int id) {
        for (User u : users) if (u.getID() == id) return u;
        return null;
    }

    // Finds events by id (Case sensitive)
    private Event findEventById(String id) {
        for (Event e : events) if (e.getEventId().equalsIgnoreCase(id)) return e;
        return null;
    }

    // Sample data provided in the gui when the program starts
    private void seedSampleData() {
        // Add sample user
        try {
            users.add(new User("Aiden", "Gabriel", 8, 30, 2007, 138849));
        } catch (Exception ignored) {}

        // Add sample event (safe defaults)
        try {
            LocalDateTime dt = LocalDateTime.now().plusDays(1).withHour(18).withMinute(0);
            Event sample = new Concert("Sample Concert", dt, "Auditorium", 2, 0);
            // ensure event has an ID — subclasses in your project may set it in their constructor;
            // if constructor signature in your version is different adjust accordingly.
            events.add(sample);
        } catch (Exception ignored) {}
        refreshEvents();
        refreshUsers();
        refreshBookings();
    }
}