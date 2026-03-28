package com.example.oppprogrammingfinalrepo;

// imports user packages of different java files
import Booking_Management.Booking;
import Event_Management.Concert;
import Event_Management.Event;
import Event_Management.Seminar;
import Event_Management.Workshop;
import User_Management.User;
import enums.BookingStatus;
import enums.EventType;
import Waitlist_Management.WaitlistManager;
import Waitlist_Management.PromotionResult;

// imports JavaFX classes to build the gui
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

    // Stores all events created in the program
    private final List<Event> events = new ArrayList<>();

    // Stores all users created in the program
    private final List<User> users = new ArrayList<>();

    // Stores all bookings using a string key and booking object
    // Hashmap allows for quick lookup by their ID
    private final Map<String, Booking> bookings = new HashMap<>();

    // Date/time format used for parsing and display
    private static final DateTimeFormatter INPUT_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"); // Used for reading input from user
    private static final DateTimeFormatter DISPLAY_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"); // Used for showing in the UI

    // Manages the waitlist system for the events that are full
    private final WaitlistManager waitlistManager = new WaitlistManager();

    // UI controls referenced across methods
    // Displays the designated text in different spots of the program
    // Textboxes
    private TextArea eventOutput;
    private TextArea userOutput;
    private TextArea bookingOutput;

    // Dropdown menus in the GUI
    // Allows user to select from a selection of items in the dropdown
    // They can select different users already inside the system or event type
    private ComboBox<String> bookingUserCombo;
    private ComboBox<String> bookingEventCombo;

    @Override
    public void start(Stage stage) {

        // Main Menu displayed on the main menu screen
        Label title = new Label("OPP Final Project - Main Menu");

        // Creates buttons for each topic so the user can navigate to each section
        Button eventBtn = new Button("Event Management");
        Button userBtn = new Button("User Management");
        Button waitlistBtn = new Button("Waitlist (View Only)");
        Button bookingBtn = new Button("Booking Management");

        // Sets the width of each button
        eventBtn.setPrefWidth(200);
        userBtn.setPrefWidth(200);
        waitlistBtn.setPrefWidth(200);
        bookingBtn.setPrefWidth(200);

        // VBox arranges the components vertically with 12 pixel spacing
        // Basis of the main menu layout
        VBox mainRoot = new VBox(12, title, eventBtn, userBtn, waitlistBtn, bookingBtn);

        // Adds padding around the VBox so the elements are not touching the windows edge
        mainRoot.setPadding(new Insets(18));

        // Creates the main menu screen to be 600 width and 420 height
        Scene mainScene = new Scene(mainRoot, 600, 420);


        // Event Screen
        eventOutput = new TextArea(); // Text area to display event info
        eventOutput.setEditable(false); // prevents the user from editing the text
        eventOutput.setPrefHeight(160); // sets a fixed height for the output display area

        // Inputs
        // Text fields for where the user enters certain info
        TextField evTitle = new TextField(); // Text field for the event title
        evTitle.setPromptText("Title");

        TextField evDate = new TextField(); // Text field for the dateTime
        // Format should be same as shown on the screen
        evDate.setPromptText("DateTime (yyyy-MM-dd HH:mm)");

        TextField evLocation = new TextField(); // Text field for the location
        evLocation.setPromptText("Location");

        TextField evCapacity = new TextField(); // Text field for the capacity
        evCapacity.setPromptText("Capacity (integer)");

        // Event selection
        // Dropdown menu for selecting each subclass type
        ComboBox<EventType> evType = new ComboBox<>();
        // Adds the set items into the dropdown menu
        evType.setItems(FXCollections.observableArrayList(EventType.CONCERT, EventType.SEMINAR, EventType.WORKSHOP));
        evType.setValue(EventType.CONCERT); // Sets the dropdown first to CONCERT

        // Extra fields used for subclass constructors
        // For example, Concert requiring an age restriction
        TextField extraField = new TextField();
        extraField.setPromptText("Extra (age/topic/materials)");

        // Buttons
        Button createEventBtn = new Button("Create Event");
        Button refreshEventsBtn = new Button("Refresh List");
        TextField cancelEventId = new TextField(); // Field where the user enters an ID to the event they want to cancel
        cancelEventId.setPromptText("Event ID to cancel");
        Button cancelEventBtn = new Button("Cancel Event");
        Button backFromEvent = new Button("Back");

        // First row of the event containing basic event details
        HBox evFormRow1 = new HBox(8, evTitle, evDate, evLocation);

        // Second row of event containing subclass information
        HBox evFormRow2 = new HBox(8, evCapacity, evType, extraField);

        // Third row for different controls like creating the event, refresh button or canceling
        HBox evFormRow3 = new HBox(8, createEventBtn, refreshEventsBtn, cancelEventId, cancelEventBtn);

        // Builds the full event management screen layout
        // Stacks the specific items, like HBox and labels
        VBox eventRoot = new VBox(10, new Label("Event Management"), eventOutput, evFormRow1, evFormRow2, evFormRow3, backFromEvent);
        eventRoot.setPadding(new Insets(12)); // Adds padding around the event screen layout
        Scene eventScene = new Scene(eventRoot, 800, 500); // Creates the scene for the Event Management screen


        // User Screen
        userOutput = new TextArea(); // Text area to display user related messages
        userOutput.setEditable(false); // Prevents user from typing inside output area
        userOutput.setPrefHeight(200); // Sets a fixed height for the display area

        // User inputs
        TextField userName = new TextField(); // Text field for first name
        userName.setPromptText("Given name");

        TextField userSurname = new TextField(); // Text field for last name (surname)
        userSurname.setPromptText("Surname");

        TextField userMonth = new TextField(); // Text field for month of their birthday
        userMonth.setPromptText("MM");

        TextField userDay = new TextField(); // Text field for day of their birthday
        userDay.setPromptText("DD");

        TextField userYear = new TextField(); // Text field for year of their birthday
        userYear.setPromptText("YYYY");

        TextField userIdField = new TextField(); // Text field for student number
        userIdField.setPromptText("ID (integer, <=999999)");

        // Buttons
        Button addUserBtn = new Button("Add User");
        Button refreshUsersBtn = new Button("Refresh Users");
        TextField removeUserId = new TextField(); // Text field where the user enters the ID of a user to remove
        removeUserId.setPromptText("User ID to remove");
        Button removeUserBtn = new Button("Remove User");
        Button backFromUser = new Button("Back");

        // First horizontal row containing all user input fields
        HBox userFormRow = new HBox(8, userName, userSurname, userMonth, userDay, userYear, userIdField);

        // Second horizontal row containing action buttons and removal field
        HBox userFormRow2 = new HBox(8, addUserBtn, refreshUsersBtn, removeUserId, removeUserBtn);

        // Vertical layout that builds the full User Management screen
        VBox userRoot = new VBox(10, new Label("User Management"), userOutput, userFormRow, userFormRow2, backFromUser);

        userRoot.setPadding(new Insets(12)); // Padding - 12 pixels of whitespace on all sides so it doesn't touch edges
        Scene userScene = new Scene(userRoot, 900, 520); // Creates the scene used for the User Management screen


        // Waitlist Viewer (read only)
        TextArea waitlistOutput = new TextArea(); // Text area to display waitlist information
        waitlistOutput.setEditable(false); // Prevents editing since this screen is only used for viewing
        waitlistOutput.setPrefHeight(400); // Sets the display height of the waitlist output

        Button backFromWaitlist = new Button("Back"); // Back button to return to main menu

        // Creates the layout for the waitlist viewer screen
        VBox waitRoot = new VBox(10, new Label("Waitlist View (per event)"), waitlistOutput, backFromWaitlist);
        waitRoot.setPadding(new Insets(12)); // Adds padding around the layout
        Scene waitScene = new Scene(waitRoot, 700, 500); // Creates the scene used for the waitlist viewer


        // Booking Screen
        bookingOutput = new TextArea(); // Text area used to display booking related messages
        bookingOutput.setEditable(false); // Prevents user from typing in output area
        bookingOutput.setPrefHeight(200); // Sets a fixed height for the booking display area

        // Dropdown menu for selecting a user when creating a booking
        bookingUserCombo = new ComboBox<>();
        bookingUserCombo.setPromptText("Select user (ID - name)");

        // Dropdown menu for selecting
        bookingEventCombo = new ComboBox<>();
        bookingEventCombo.setPromptText("Select event (ID - title)");

        // Text field for entering the unique booking ID
        TextField bookingIdField = new TextField();
        bookingIdField.setPromptText("Booking ID (unique)");

        // Buttons
        Button createBookingBtn = new Button("Create Booking");
        Button refreshBookingsBtn = new Button("Refresh Bookings");
        TextField cancelBookingId = new TextField(); // Text field for where the user enters the booking ID to cancel
        cancelBookingId.setPromptText("Booking ID to cancel");
        Button cancelBookingBtn = new Button("Cancel Booking");
        Button backFromBooking = new Button("Back");

        // First row contains the booking creation inputs
        HBox bookingRow1 = new HBox(8, bookingUserCombo, bookingEventCombo, bookingIdField);

        // Second row contains booking action buttons and cancellation input
        HBox bookingRow2 = new HBox(8, createBookingBtn, refreshBookingsBtn, cancelBookingId, cancelBookingBtn);

        // Main vertical layout for the Booking Management screen
        VBox bookingRoot = new VBox(10, new Label("Booking Management"), bookingOutput, bookingRow1, bookingRow2, backFromBooking);
        bookingRoot.setPadding(new Insets(12)); // Adds padding around the booking layout
        Scene bookingScene = new Scene(bookingRoot, 900, 520); // Creates the scene used for the Booking Management screen


        // Navigation between the screens
        // Opens the Event Management screen after refreshing its displayed data
        eventBtn.setOnAction(e -> {
            refreshEvents(); // Ensures list is updated
            stage.setScene(eventScene); // Switches scene
        });

        // Opens the User Management screen after refreshing its displayed data
        userBtn.setOnAction(e -> {
            refreshUsers();
            stage.setScene(userScene);
        });

        // Opens the Waitlist Viewer screen after refreshing its displayed data
        waitlistBtn.setOnAction(e -> {
            refreshWaitlist(waitlistOutput);
            stage.setScene(waitScene);
        });

        // Opens the Booking Management screen after refreshing its displayed data
        bookingBtn.setOnAction(e -> {
            refreshBookings();
            stage.setScene(bookingScene);
        });

        // Returns from each management screen back to main menu screen
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

                waitlistManager.handleEventCancelled(found);
                found.getWaitlist().clear();
                found.getConfirmedBookings().clear();
                refreshEvents();

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

// count how many confirmed bookings this user already has
                int confirmedCount = countConfirmedBookings(u);

// booking limits
                int limit = switch (u.getUserType()) {
                    case STUDENT -> 3;
                    case STAFF -> 5;
                    case GUEST -> 1;
                };

// only confirm if under limit AND event has capacity
                if (ev.hasCapacity() && confirmedCount < limit) {

                    newBooking = new Booking(bookingId, u, ev, BookingStatus.CONFIRMED);
                    ev.addConfirmedBooking(newBooking);
                    waitlistManager.addToConfirmed(ev, u);

                }
                else {

                    newBooking = new Booking(bookingId, u, ev, BookingStatus.WAITLISTED);
                    ev.addToWaitlist(newBooking);
                    waitlistManager.addToWaitlist(ev, u);
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

                PromotionResult result = waitlistManager.cancelConfirmedWithResult(ev, b.getUser());

                if (result.isPromoted()) {
                    User promotedUser = result.getPromotedUser();

                    Booking promotedBooking = null;
                    for (Booking wb : ev.getWaitlist()) {
                        if (wb.getUser().equals(promotedUser)) {
                            promotedBooking = wb;
                            break;
                        }
                    }

                    if (promotedBooking != null) {
                        ev.removeFromWaitlist(promotedBooking);
                        promotedBooking.setStatus(BookingStatus.CONFIRMED);
                        ev.addConfirmedBooking(promotedBooking);
                    }
                }

            } else {
                ev.removeFromWaitlist(b);
                waitlistManager.removeFromWaitlist(ev, b.getUser());
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

    // Outputs a refreshed version of the events screen
    private void refreshEvents() {
        // StringBuilder is used to build the display text
        StringBuilder sb = new StringBuilder();

        // Loops through every event in the event list
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

        eventOutput.setText(sb.toString()); // Displays the text in the event output area

        // Update the booking dropdowns so they match the latest data
        refreshBookingCombos();
    }


    // Refreshes the user display area with the latest user information
    private void refreshUsers() {
        // StringBuilder is used to build the display text
        StringBuilder sb = new StringBuilder();

        // Loops through every user in the user list
        for (User u : users) {
            sb.append(u.getID())
                    .append(" - ").append(u.getName())
                    .append(" ").append(u.getSurname())
                    .append(" | ").append(u.getBirthdate())
                    .append("\n");
        }

        userOutput.setText(sb.toString()); // Displays completed text in the  user output area

        // Update the booking dropdowns in case the user list changed
        refreshBookingCombos();
    }


    // Refreshes the booking display area with the latest booking information
    private void refreshBookings() {
        // StringBuilder is used to build the display text
        StringBuilder sb = new StringBuilder();

        // Loops through every booking stored in the booking map
        for (Booking b : bookings.values()) {
            sb.append(b.getBookingId())
                    .append(" | user=").append(b.getUser().getID())
                    .append(" | event=").append(b.getEvent().getEventId())
                    .append(" | status=").append(b.getStatus())
                    .append("\n");
        }

        bookingOutput.setText(sb.toString()); // Displays the completed text in the booking output area

        // Update the booking dropdowns so the UI stays consistent
        refreshBookingCombos();
    }


    // Refreshes the waitlist viewer with confirmed users and waitlisted users for each event
    private void refreshWaitlist(TextArea waitlistOutput) {
        StringBuilder sb = new StringBuilder();

        // Loop through every event in the system
        for (Event ev : events) {
            sb.append("Event ").append(ev.getEventId()).append(" - ").append(ev.getTitle()).append("\n");

            // Display confirmed users for this event
            sb.append(" Confirmed:\n");
            for (User u : waitlistManager.viewConfirmed(ev)) {
                sb.append("  - user=").append(u.getID()).append(" ").append(u.getName()).append("\n");
            }

            // Display waitlisted users for this event
            sb.append(" Waitlist:\n");
            for (User u : waitlistManager.viewWaitlist(ev)) {
                sb.append("  - user=").append(u.getID()).append(" ").append(u.getName()).append("\n");
            }

            // Add a blank line between events for readability
            sb.append("\n");
        }

        // Display the completed text in the waitlist output area
        waitlistOutput.setText(sb.toString());
    }


    // Updates the dropdown menus with current users and events
    private void refreshBookingCombos() {
        // Creates a list of user display strings for the user ComboBox
        List<String> uList = new ArrayList<>();
        for (User u : users) {
            uList.add(u.getID() + " - " + u.getName());
        }

        // Replace the items inside the user comboBox
        bookingUserCombo.setItems(FXCollections.observableArrayList(uList));

        // Creates a list of event display strings for the event ComboBox
        List<String> evList = new ArrayList<>();
        for (Event ev : events) {
            evList.add(ev.getEventId() + " - " + ev.getTitle());
        }

        // Replace the current items in the event ComboBox
        bookingEventCombo.setItems(FXCollections.observableArrayList(evList));
    }


    // Searches for a user by ID and returns the matching User object
    private User findUserById(int id) {
        for (User u : users) if (u.getID() == id) return u;
        return null; // Returns NULL if no users matching user ID is found
    }


    // Searches for an event by ID and returns the matching Event object (Case Sensitive)
    private Event findEventById(String id) {
        for (Event e : events) if (e.getEventId().equalsIgnoreCase(id)) return e;
        return null;
    }

    private int countConfirmedBookings(User user) {

        int count = 0;

        for (Booking b : bookings.values()) {

            if (b.getUser().equals(user)
                    && b.getStatus() == BookingStatus.CONFIRMED) {

                count++;
            }
        }

        return count;
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