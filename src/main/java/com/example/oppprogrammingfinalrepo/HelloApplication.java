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
import enums.UserType;
import Waitlist_Management.WaitlistManager;
import Waitlist_Management.PromotionResult;

// imports JavaFX classes to build the gui
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Polygon;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

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
    private TextArea waitlistOutput;

    // Dropdown menus in the GUI
    // Allows user to select from a selection of items in the dropdown
    // They can select different users already inside the system or event type
    private ComboBox<String> bookingUserCombo;
    private ComboBox<String> bookingEventCombo;

    @Override
    public void start(Stage stage) {
        // Array to store all scenes
        Scene[] scenes = new Scene[5]; // 0=main, 1=event, 2=user, 3=wait, 4=booking

        // Declaration of the scenes for each part of the application
        Scene mainScene;
        Scene eventScene;
        Scene userScene;
        Scene waitScene;
        Scene bookingScene;

        // University of Guelph colour theme used throughout the GUI
        String pageBackgroundStyle = "-fx-background-color: #ECECEC;";
        String whiteTopBarStyle = "-fx-background-color: white;";
        String blackHeaderStyle = "-fx-background-color: black;";
        String navButtonStyle = "-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold; -fx-padding: 10 18 10 18;";        String featuredCardStyle = "-fx-background-color: white; -fx-background-radius: 8; -fx-border-color: #D9D9D9; -fx-border-radius: 8;";
        String redActionButtonStyle = "-fx-background-color: #C20430; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-background-radius: 4;";

        // Main Menu displayed on the main menu screen
        Label title = new Label("UNIVERSITY OF\nGUELPH");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 30px; -fx-font-weight: bold;");

        title.setTranslateX(-75); // Moves it left by 75 pixels

        // Gets the UofG logo from the resource folder
        Image logoImage = new Image(getClass().getResourceAsStream("/Uofg_logo.png"));

        // Loads the image and sets the height
        ImageView logoView = new ImageView(logoImage);
        logoView.setFitHeight(85);
        logoView.setPreserveRatio(true);

        logoView.setTranslateX(-75); // Moves it left by 75 pixels

        // Creates buttons for each topic so the user can navigate to each section
        Button eventBtn = new Button("Events");
        Button userBtn = new Button("Users");
        Button waitlistBtn = new Button("Waitlist");
        Button bookingBtn = new Button("Bookings");

        // Styles the navigation buttons so they look like website navigation links
        eventBtn.setStyle(navButtonStyle);
        userBtn.setStyle(navButtonStyle);
        waitlistBtn.setStyle(navButtonStyle);
        bookingBtn.setStyle(navButtonStyle);

        // Button event handlers to navigate between different application screens
        eventBtn.setOnAction(e -> {
            refreshEvents(); // Update event data before displaying the screen
            stage.setScene(scenes[1]); // Switches to Event Management Screen
        });

        userBtn.setOnAction(e -> {
            refreshUsers(); // Update user data before displaying the screen
            stage.setScene(scenes[2]); // Switches to User Management screen
        });

        waitlistBtn.setOnAction(e -> {
            refreshWaitlist(waitlistOutput); // Update waitlist display before showing it
            stage.setScene(scenes[3]); // Switches to Waitlist screen
        });

        bookingBtn.setOnAction(e -> {
            refreshBookings(); // Update booking data before displaying the screen
            stage.setScene(scenes[4]); // Switches to Booking Management screen
        });

        // Same top as the official UofG website excluding our buttons
        // White strip at the very top
        Region whiteTopBar = new Region();
        whiteTopBar.setPrefHeight(26);
        whiteTopBar.setStyle(whiteTopBarStyle);

        // Red triangular accent
        Polygon redAccent = new Polygon(
                -15,-10,
                25,-10,
                80,105,
                -15,105
        );
        redAccent.setFill(Color.web("#C20430")); // Sets colour to red

        // Yellow triangular accent
        Polygon goldAccent = new Polygon(
                -15,-10,
                70,-10,
                -15,105
        );
        goldAccent.setFill(Color.web("#FFC72C")); // Sets colour to yellow

        // Create a Pane container to hold the accent shapes (red and gold triangles)
        // A Pane allows manual positioning of shapes without automatic layout adjustments
        Pane accentPane = new Pane();
        accentPane.setPrefSize(140, 95); // Set the preferred size of the Pane so it occupies a consistent space in the header
        accentPane.setMinSize(140, 95); // Prevent the Pane from shrinking smaller than the defined size
        accentPane.setMaxSize(140, 95); // Prevent the Pane from expanding larger than the defined size

        // Added the accent shapes to the pane in the order they should be place
        // Since gold was added first, the red can cover it as it is on the official website
        accentPane.getChildren().addAll(goldAccent, redAccent);

        // Container that holds the logo image and the "UNIVERSITY OF GUELPH" text side-by-side
        // The spacing value (12) controls the horizontal gap between the logo and the title
        HBox logoAndTitleBox = new HBox(12, logoView, title);
        logoAndTitleBox.setAlignment(Pos.CENTER_LEFT); // Aligns the logo and text vertically towards the left

        // Container that groups the accent triangles and the logo/title area together
        // The spacing value (18) controls the distance between the accent shapes and the branding
        HBox leftBranding = new HBox(18, accentPane, logoAndTitleBox);
        leftBranding.setPadding(new Insets(10, 20, 10, 12)); // Adds padding so it does not touch the edges of the header
        leftBranding.setStyle(blackHeaderStyle); // applies black background style used for the top header bar
        leftBranding.setAlignment(Pos.CENTER_LEFT); // positioned left and vertically centered

        // Creates the navigation bar that holds the screen navigation buttons
        // Horizontal navigation bar to create a website feel
        // 45 pixel spacing between each button
        HBox navBar = new HBox(45, eventBtn, userBtn, waitlistBtn, bookingBtn);
        navBar.setPadding(new Insets(0, 0, 0, 120)); // padding so the bar is pushed more to the right
        navBar.setStyle(blackHeaderStyle); // Applies same black background style to the rest of the header
        navBar.setAlignment(Pos.CENTER); // Centers navigation buttons vertically and horizontally within the nav bar
        HBox.setHgrow(navBar, Priority.ALWAYS); // Allows nav bar to expand and fill any unfilled horizontal space

        // Combines the branding section (triangles + logo/title) and the navigation bar
        // This forms the full header row displayed at the top of the application
        HBox headerBar = new HBox(leftBranding, navBar);
        headerBar.setStyle(blackHeaderStyle); // Applies the same black background style so the entire row appears as one header
        headerBar.setAlignment(Pos.CENTER_LEFT); // Keeps all header content vertically centered and aligned from the left
        HBox.setHgrow(navBar, Priority.ALWAYS); // Ensures the navigation section grows if the window width increases

        // Featured/current event section title
        Label featuredTitle = new Label("Featured / Current Event");
        featuredTitle.setStyle("-fx-font-size: 34px; -fx-font-weight: bold; -fx-text-fill: black;");

        // Featured/current event content
        Label featuredBody = new Label(
                "Welcome to the University of Guelph Event Booking System.\n\n" +
                        "Use the navigation above to manage events, users, waitlists, and bookings.\n" +
                        "Current featured item: Sample Concert and active campus event management."
        );
        featuredBody.setStyle("-fx-font-size: 16px; -fx-text-fill: black;");

        // Action button under featured area
        Button featuredActionBtn = new Button("Try Again");
        featuredActionBtn.setStyle(redActionButtonStyle); // Apply the red styling used on the official page
        featuredActionBtn.setPrefWidth(160); // fixed width of button
        featuredActionBtn.setPrefHeight(45); // fixed height of button
        featuredActionBtn.setDisable(true); // disabled button for now so it can be implemented later

        // Container for the featured content card on the homepage
        // The spacing value (18) controls the vertical gap between the title, description, and button
        VBox featuredCard = new VBox(18, featuredTitle, featuredBody, featuredActionBtn);
        featuredCard.setPadding(new Insets(35)); // Adds padding inside the card so the content does not touch the edges
        featuredCard.setStyle(featuredCardStyle); // Applies the styling used for the featured card (background, border, etc.)

        // Main vertical layout for the home screen
        // Layout is like the official website, white bar at the top, main header bar and added featured card
        VBox mainRoot = new VBox(0, whiteTopBar, headerBar, featuredCard);
        mainRoot.setStyle(pageBackgroundStyle); // Applies the overall page background styling
        mainRoot.setPadding(new Insets(0, 0, 30, 0));
        featuredCard.setTranslateX(20); // Manually shifted card to the right
        featuredCard.setTranslateY(20); // Manually shifted card upwards

        // Creates the main menu screen to be wider like a website page
        mainScene = new Scene(mainRoot, 1200, 700);
        scenes[0] = mainScene;


        // Event Screen
        eventOutput = new TextArea(); // Text area to display event info
        eventOutput.setEditable(false); // prevents the user from editing the text
        eventOutput.setPrefHeight(220); // sets a fixed height for the output display area

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
        Label eventHeader = new Label("Event Management"); // Section header label displayed at the top of the Event Management screen

        // Apply styling to the header text to make it stand out
        // Uses a larger font size, bold weight, and the university red color for consistency
        eventHeader.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #C20430;");

        // Create the top header section (navigation bar with buttons and logo)
        VBox eventHeaderTop = createHeader(whiteTopBarStyle, blackHeaderStyle, navButtonStyle, eventBtn, userBtn, waitlistBtn, bookingBtn, logoImage);

        // Main content layout for Event Management screen (form inputs + output display)
        VBox eventContent = new VBox(10, eventHeader, eventOutput, evFormRow1, evFormRow2, evFormRow3, backFromEvent);
        eventContent.setPadding(new Insets(12));

        // Root layout combining header and content vertically
        VBox eventRoot = new VBox(0, eventHeaderTop, eventContent);
        eventRoot.setStyle(pageBackgroundStyle); // Apply background styling to entire screen

        // Adds styling to the specific buttons
        createEventBtn.setStyle("-fx-background-color: white; -fx-text-fill: black; -fx-border-color: #BDBDBD;");
        refreshEventsBtn.setStyle("-fx-background-color: white; -fx-text-fill: black; -fx-border-color: #BDBDBD;");
        cancelEventBtn.setStyle("-fx-background-color: white; -fx-text-fill: black; -fx-border-color: #BDBDBD;");
        backFromEvent.setStyle("-fx-background-color: white; -fx-text-fill: black; -fx-border-color: #BDBDBD;");

        // Create the Event Management scene and assign it to index 1 in the scenes array
        eventScene = new Scene(eventRoot, 1200, 700);
        scenes[1] = eventScene;


        // User Screen
        userOutput = new TextArea(); // Text area to display user related messages
        userOutput.setEditable(false); // Prevents user from typing inside output area
        userOutput.setPrefHeight(240); // Sets a fixed height for the display area

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

        TextField userEmail = new TextField(); // Text field for email
        userEmail.setPromptText("Email");

        // Dropdown menu to choose which user is being created
        ComboBox<UserType> userTypeCombo = new ComboBox<>();
        userTypeCombo.getItems().addAll(UserType.STUDENT, UserType.STAFF, UserType.GUEST);
        userTypeCombo.setValue(UserType.STUDENT); // Defaults to student originally

        Label userTypeLabel = new Label("User Type:"); // Label displayed for the dropdown

        TextField removeUserId = new TextField(); // Text field where the user enters the ID of a user to remove
        removeUserId.setPromptText("User ID to remove");

        TextField viewUserId = new TextField(); // Text field where the user can view another user by their id
        viewUserId.setPromptText("User ID to view");

        // Buttons
        Button addUserBtn = new Button("Add User");
        Button refreshUsersBtn = new Button("Refresh Users");
        Button removeUserBtn = new Button("Remove User");
        Button backFromUser = new Button("Back");
        Button viewUserBtn = new Button("View User Details");

        // First horizontal row containing all user input fields
        HBox userFormRow = new HBox(8, userName, userSurname, userEmail, userMonth, userDay, userYear, userIdField, userTypeLabel, userTypeCombo);

        // Second horizontal row containing action buttons and removal field
        HBox userFormRow2 = new HBox(8, addUserBtn, refreshUsersBtn, viewUserId, viewUserBtn, removeUserId, removeUserBtn);

        // Vertical layout that builds the full User Management screen
        Label userHeader = new Label("User Management"); // Section header label displayed at the top of the User Management screen

        // Apply styling to the header text to make it stand out
        // Uses a larger font size, bold weight, and the university red color for consistency
        userHeader.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #C20430;");

        // Create the top header section (navigation bar with buttons and logo)
        VBox userHeaderTop = createHeader(whiteTopBarStyle, blackHeaderStyle, navButtonStyle, eventBtn, userBtn, waitlistBtn, bookingBtn, logoImage);

        // Main content layout for User Management screen (user form inputs + output display)
        VBox userContent = new VBox(10, userHeader, userOutput, userFormRow, userFormRow2, backFromUser);
        userContent.setPadding(new Insets(12)); // Padding - 12 pixels of whitespace on all sides so it doesn't touch edges

        // Root layout combining header and content vertically
        VBox userRoot = new VBox(0, userHeaderTop, userContent);
        userRoot.setStyle(pageBackgroundStyle); // Applies the overall page background style used across the application

        // Adds styling for the specific buttons
        addUserBtn.setStyle("-fx-background-color: white; -fx-text-fill: black; -fx-border-color: #BDBDBD;");
        refreshUsersBtn.setStyle("-fx-background-color: white; -fx-text-fill: black; -fx-border-color: #BDBDBD;");
        removeUserBtn.setStyle("-fx-background-color: white; -fx-text-fill: black; -fx-border-color: #BDBDBD;");
        viewUserBtn.setStyle("-fx-background-color: white; -fx-text-fill: black; -fx-border-color: #BDBDBD;");
        backFromUser.setStyle("-fx-background-color: white; -fx-text-fill: black; -fx-border-color: #BDBDBD;");

        // Create the User Management scene and assign it to index 2 in the scenes array
        userScene = new Scene(userRoot, 1200, 700);
        scenes[2] = userScene;


        // Waitlist Viewer (read only)
        waitlistOutput = new TextArea(); // Text area to display waitlist information
        waitlistOutput.setEditable(false); // Prevents editing since this screen is only used for viewing
        waitlistOutput.setPrefHeight(420); // Sets the display height of the waitlist output

        Button backFromWaitlist = new Button("Back"); // Back button to return to main menu

        // Creates the layout for the waitlist viewer screen
        Label waitHeader = new Label("Waitlist View (per event)"); // Section header label displayed at the top of the Waitlist Management screen

        // Apply styling to the header text to make it stand out
        // Uses a larger font size, bold weight, and the university red color for consistency
        waitHeader.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #C20430;");

        // Create the top header section (navigation bar with buttons and logo)
        VBox waitHeaderTop = createHeader(whiteTopBarStyle, blackHeaderStyle, navButtonStyle, eventBtn, userBtn, waitlistBtn, bookingBtn, logoImage);

        // Main content layout for Waitlist screen (displays waitlisted bookings)
        VBox waitContent = new VBox(10, waitHeader, waitlistOutput, backFromWaitlist);
        waitContent.setPadding(new Insets(12)); // Adds padding around the layout

        // Root layout combining header and content vertically
        VBox waitRoot = new VBox(0, waitHeaderTop, waitContent);
        waitRoot.setStyle(pageBackgroundStyle); // Applies the overall page background style used across the application

        // Adds styling for the back button
        backFromWaitlist.setStyle("-fx-background-color: white; -fx-text-fill: black; -fx-border-color: #BDBDBD;");

        // Create the Waitlist scene and assign it to index 3 in the scenes array
        waitScene = new Scene(waitRoot, 1200, 700);
        scenes[3] = waitScene;


        // Booking Screen
        bookingOutput = new TextArea(); // Text area used to display booking related messages
        bookingOutput.setEditable(false); // Prevents user from typing in output area
        bookingOutput.setPrefHeight(240); // Sets a fixed height for the booking display area

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
        Label bookingHeader = new Label("Booking Management"); // Section header label displayed at the top of the Booking Management screen

        // Apply styling to the header text to make it stand out
        // Uses a larger font size, bold weight, and the university red color for consistency
        bookingHeader.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #C20430;");

        // Create the top header section (navigation bar with buttons and logo)
        VBox bookingHeaderTop = createHeader(whiteTopBarStyle, blackHeaderStyle, navButtonStyle, eventBtn, userBtn, waitlistBtn, bookingBtn, logoImage);

        // Main content layout for Booking Management screen (booking form inputs + output display)
        VBox bookingContent = new VBox(10, bookingHeader, bookingOutput, bookingRow1, bookingRow2, backFromBooking);
        bookingContent.setPadding(new Insets(12)); // Adds padding around the layout

        // Root layout combining header and content vertically
        VBox bookingRoot = new VBox(0, bookingHeaderTop, bookingContent);
        bookingRoot.setStyle(pageBackgroundStyle); // Applies the overall page background style used across the application

        // Adds styling for the specific buttons
        createBookingBtn.setStyle("-fx-background-color: white; -fx-text-fill: black; -fx-border-color: #BDBDBD;");
        refreshBookingsBtn.setStyle("-fx-background-color: white; -fx-text-fill: black; -fx-border-color: #BDBDBD;");
        cancelBookingBtn.setStyle("-fx-background-color: white; -fx-text-fill: black; -fx-border-color: #BDBDBD;");
        backFromBooking.setStyle("-fx-background-color: white; -fx-text-fill: black; -fx-border-color: #BDBDBD;");

        // Create the Booking Management scene and assign it to index 4 in the scenes array
        bookingScene = new Scene(bookingRoot, 1200, 700);
        scenes[4] = bookingScene;

        // Returns from each management screen back to main menu screen
        backFromEvent.setOnAction(e -> stage.setScene(mainScene));
        backFromUser.setOnAction(e -> stage.setScene(mainScene));
        backFromWaitlist.setOnAction(e -> stage.setScene(mainScene));
        backFromBooking.setOnAction(e -> stage.setScene(mainScene));


        // Actions: EVENTS
        // Runs when the user clicks the "Create Event" Button
        createEventBtn.setOnAction(e -> {
            try {
                // Reads and trims user input if valid input
                String titleVal = evTitle.getText().trim();
                LocalDateTime dt = LocalDateTime.parse(evDate.getText().trim(), INPUT_FMT);
                String locVal = evLocation.getText().trim();
                int cap = Integer.parseInt(evCapacity.getText().trim());
                EventType t = evType.getValue();

                // Will store the specific Event subclass object that gets created
                Event created;

                // Creates correct subclasses based on the type of event
                switch (t) {
                    case CONCERT -> {
                        // Checks if the age restriction inputted was an integer value
                        // Otherwise defaults to 0 if left blank
                        int age = 0;
                        try { age = Integer.parseInt(extraField.getText().trim()); } catch (Exception ex) { age = 0; }
                        created = new Concert(titleVal, dt, locVal, cap, age);
                    }
                    case SEMINAR -> {
                        // Automatically has a default topic if field is left blank
                        String topic = extraField.getText().trim().isEmpty() ? "General" : extraField.getText().trim();
                        created = new Seminar(titleVal, dt, locVal, cap, topic);
                    }
                    case WORKSHOP -> {
                        // Automatically has a default material field if left blank
                        String materials = extraField.getText().trim().isEmpty() ? "N/A" : extraField.getText().trim();
                        created = new Workshop(titleVal, dt, locVal, cap, materials);
                    }
                    default -> throw new IllegalStateException("Unexpected event type");
                }

                // Stores the selected event type in the created object
                created.setEventType(t);

                // Adds the new event to the list
                events.add(created);

                // Clears event inputs after creation
                evTitle.clear();
                evDate.clear();
                evLocation.clear();
                evCapacity.clear();
                extraField.clear();

                // Refresh the event screen to show the new event
                refreshEvents();
            } catch (Exception ex) {
                // Shows an error message if input is invalid or event creation fails
                eventOutput.setText("CREATE ERROR: " + ex.getClass().getSimpleName() + " - " + ex.getMessage());
            }
        });

        // Refreshes the event display when the "Refresh List" button is clicked
        refreshEventsBtn.setOnAction(e -> refreshEvents());

        // Runs when the user clicks the "Cancel Event" button
        cancelEventBtn.setOnAction(e -> {
            String id = cancelEventId.getText().trim(); // Reads the event ID entered by the user

            // Validates the ID was entered
            if (id.isEmpty()) {
                eventOutput.setText("Enter Event ID to cancel.");
                return;
            }

            Event found = findEventById(id); // Finds the matching event object

            // Shows an error message if event does not exist
            if (found == null) {
                eventOutput.setText("Event not found: " + id);
                return;
            }

            try {
                found.cancelEvent(); // Mark the event as cancelled

                // Let the waitlist manager handle cancellation related logic
                waitlistManager.handleEventCancelled(found);

                // Clears the waitlist and confirmed bookings
                found.getWaitlist().clear();
                found.getConfirmedBookings().clear();

                refreshEvents(); // Refresh the event display

                // Mark all confirmed bookings for this event as cancelled
                List<Booking> toCancel = new ArrayList<>();
                for (Booking b : new ArrayList<>(found.getConfirmedBookings())) {
                    b.setStatus(BookingStatus.CANCELLED);
                    toCancel.add(b);
                }

                found.getConfirmedBookings().clear(); // Clear confirmed bookings again

                refreshEvents(); // Refresh the event display again

                // Shows an error message if the cancellation fails
            } catch (Exception ex) {
                eventOutput.setText("CANCEL ERROR: " + ex.getMessage());
            }
        });


        // Actions: USERS
        // Runs when the user clicks the "Add User" button
        addUserBtn.setOnAction(e -> {
            // Reads and trims the input from the user
            try {
                String n = userName.getText().trim();
                String s = userSurname.getText().trim();
                int m = Integer.parseInt(userMonth.getText().trim());
                int d = Integer.parseInt(userDay.getText().trim());
                int y = Integer.parseInt(userYear.getText().trim());
                int id = Integer.parseInt(userIdField.getText().trim());
                UserType type = userTypeCombo.getValue();
                String email = userEmail.getText().trim();

                // Prevents users from being duplicated using the same id
                if (findUserById(id) != null) {
                    userOutput.setText("User ID already exists.");
                    return;
                }

                // Creates a new user object using the entered information
                User u = new User(n, s, m, d, y, id, email, type);

                users.add(u); // Adds the users to the main user list

                // Clears the input fields after adding a user
                userName.clear();
                userSurname.clear();
                userEmail.clear();
                userMonth.clear();
                userDay.clear();
                userYear.clear();
                userIdField.clear();

                refreshUsers(); // Refreshes the user display to show the new user

                // Shows an error message if input is invalid or user creation fails
            } catch (Exception ex) {
                userOutput.setText("ADD USER ERROR: " + ex.getClass().getSimpleName() + " - " + ex.getMessage());
            }
        });

        // Refreshes when the user clicks the "Remove User" button
        refreshUsersBtn.setOnAction(e -> refreshUsers());

        // Runs when the user clicks the "Remove User" button
        removeUserBtn.setOnAction(e -> {
            try {
                // Reads the user ID entered for removal
                int id = Integer.parseInt(removeUserId.getText().trim());

                User found = findUserById(id); // Finds the matching user object

                // Shows an error message if no matching user id exists
                if (found == null) {
                    userOutput.setText("User not found: " + id);
                    return;
                }

                users.remove(found); // Removes the user from the user list

                // Also remove bookings belonging to this user
                bookings.values().removeIf(b -> b.getUser().equals(found));

                refreshUsers(); // Refreshes the user display after removal

                // Shows an error message if removal fails
            } catch (Exception ex) {
                userOutput.setText("REMOVE USER ERROR: " + ex.getMessage());
            }
        });

        // Runs when user clicks view user button
        viewUserBtn.setOnAction(e -> {
            try {
                // Reads the ID entered in the "view user" screen
                int id = Integer.parseInt(viewUserId.getText().trim());
                User found = findUserById(id); // Searches the user list for a matching user id

                // If no user is found program outputs an error message
                if (found == null) {
                    userOutput.setText("User not found: " + id);
                    return;
                }

                // Stringbuilder to construct the display efficiently
                StringBuilder sb = new StringBuilder();

                // Displays the selected users information
                sb.append("User Details\n");
                sb.append("ID: ").append(found.getID()).append("\n");
                sb.append("Name: ").append(found.getName()).append(" ").append(found.getSurname()).append("\n");
                sb.append("Email: ").append(found.getEmail()).append("\n");
                sb.append("Birthdate: ").append(found.getBirthdate()).append("\n");
                sb.append("Type: ").append(found.getUserType()).append("\n\n");
                sb.append("Bookings:\n");

                boolean hasBookings = false; // Tracks weather the user has any bookings

                // Loops through all bookings in system
                for (Booking b : bookings.values()) {
                    if (b.getUser().equals(found)) {
                        // Displays booking details of event and status
                        sb.append("- ")
                                .append(b.getBookingId())
                                .append(" | event=").append(b.getEvent().getEventId())
                                .append(" | ").append(b.getEvent().getTitle())
                                .append(" | status=").append(b.getStatus())
                                .append("\n");
                        hasBookings = true;
                    }
                }

                // If user has no bookings, it displays this message
                if (!hasBookings) {
                    sb.append("No bookings found.");
                }

                userOutput.setText(sb.toString()); // Outputs the comlpleted text

            } catch (Exception ex) {
                userOutput.setText("VIEW USER ERROR: " + ex.getMessage()); // Handles invalid input
            }
        });


        // Actions: BOOKINGS
        // Runs when user clicks the "Create Booking" button
        createBookingBtn.setOnAction(e -> {
            try {
                // Reads the booking id and selected entries from the gui
                String bookingId = bookingIdField.getText().trim();
                String userEntry = bookingUserCombo.getValue();
                String eventEntry = bookingEventCombo.getValue();

                // Prevents duplicated bookings
                if (bookings.containsKey(bookingId)) {
                    bookingOutput.setText("Booking ID already exists.");
                    return;
                }

                // Makes sure all required inputs were provided
                if (bookingId.isEmpty() || userEntry == null || eventEntry == null) {
                    bookingOutput.setText("Please supply booking ID, user, and event.");
                    return;
                }

                // Gets the user ID and eventID from the combobox display
                int userId = Integer.parseInt(userEntry.split(" - ")[0]);
                String eventId = eventEntry.split(" - ")[0];

                // Finds actual user and event objects using their IDs
                User u = findUserById(userId);
                Event ev = findEventById(eventId);

                // Stops if either user or event does not exist
                if (u == null || ev == null) {
                    bookingOutput.setText("User or event not found.");
                    return;
                }

                // Only active events can be booked
                if (ev.getStatus() != enums.EventStatus.ACTIVE) {
                    bookingOutput.setText("Event is not active.");
                    return;
                }

                // Prevents the same user from booking the same event more than 1
                // unless if their previous booking was cancelled
                for (Booking b : bookings.values()) {
                    if (b.getUser().equals(u) && b.getEvent().equals(ev) && b.getStatus() != BookingStatus.CANCELLED) {
                        bookingOutput.setText("User already booked this event.");
                        return;
                    }
                }

                Booking newBooking;

                // If the event still has capacity, booking is confirmed immediately
                if (ev.hasCapacity()) {
                    newBooking = new Booking(bookingId, u, ev, BookingStatus.CONFIRMED);
                    ev.addConfirmedBooking(newBooking);

                    // Keep the waitlist manager's confirmed list in sync
                    waitlistManager.addToConfirmed(ev, u);

                } else {
                    // If event is full, the booking of the user will be on waitlist
                    newBooking = new Booking(bookingId, u, ev, BookingStatus.WAITLISTED);
                    ev.addToWaitlist(newBooking);

                    // Keep the waitlist manager's confirmed list in sync
                    waitlistManager.addToWaitlist(ev, u);
                }

                // Stores the booking in the main list
                bookings.put(bookingId, newBooking);

                // Refreshes the booking and event displays
                refreshBookings();
                refreshEvents();

                // Shows an error message if booking fails
            } catch (Exception ex) {
                bookingOutput.setText("CREATE BOOKING ERROR: " + ex.getClass().getSimpleName() + " - " + ex.getMessage());
            }
        });

        // Refreshes the booking display when the "Refresh Bookings" button is clicked
        refreshBookingsBtn.setOnAction(e -> refreshBookings());

        // Runs when the user clicks the "Cancel Booking" button
        cancelBookingBtn.setOnAction(e -> {
            // Reads the booking ID the user entered
            String id = cancelBookingId.getText().trim();

            // Validates the string was not empty
            if (id.isEmpty()) {
                bookingOutput.setText("Enter booking ID to cancel.");
                return;
            }

            // lookup the booking id in the bookings map
            Booking b = bookings.get(id);

            // Error message if booking is not found
            if (b == null) {
                bookingOutput.setText("Booking not found: " + id);
                return;
            }

            // Error message if the booking is already cancelled
            if (b.getStatus() == BookingStatus.CANCELLED) {
                bookingOutput.setText("Already cancelled.");
                return;
            }

            // Gets the event linked to this booking
            Event ev = b.getEvent();

            // Checks to see if the booking was confirmed
            boolean wasConfirmed = b.getStatus() == BookingStatus.CONFIRMED;

            // Marks the booking as cancelled and removes it from the booking map
            b.setStatus(BookingStatus.CANCELLED);
            bookings.remove(id);

            if (wasConfirmed) {
                ev.removeConfirmedBooking(b); // Removes cancelled booking from events confirmed list

                // Lets the waitlist manager cancel the confirmed users list
                // Also promotes any user if availible
                PromotionResult result = waitlistManager.cancelConfirmedWithResult(ev, b.getUser());

                if (result.isPromoted()) {
                    User promotedUser = result.getPromotedUser();

                    // finds promoted users booking in the event waitlist
                    Booking promotedBooking = null;
                    for (Booking wb : ev.getWaitlist()) {
                        if (wb.getUser().equals(promotedUser)) {
                            promotedBooking = wb;
                            break;
                        }
                    }

                    // Move the booking from waitlist to confirmed
                    if (promotedBooking != null) {
                        ev.removeFromWaitlist(promotedBooking);
                        promotedBooking.setStatus(BookingStatus.CONFIRMED);
                        ev.addConfirmedBooking(promotedBooking);
                    }
                }

                // If booking was only waitlisted, remove it from event waitlist
            } else {
                ev.removeFromWaitlist(b);
                waitlistManager.removeFromWaitlist(ev, b.getUser());
            }

            // Refresh booking and event displays after cancellation
            refreshBookings();
            refreshEvents();
        });

        // Initial sample data
        seedSampleData();

        // Configure and show the main application window
        stage.setTitle("OPP Final Project");
        stage.setScene(mainScene);
        stage.show();
    }


    // Helper methods

    // Outputs a refreshed version of the events screen
    private void refreshEvents() {
        // StringBuilder is used to build the display text
        StringBuilder sb = new StringBuilder();

        events.sort(Comparator.comparing(Event::getDateTime)); // Sorts by Date

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

            // Loops through all confirmed bookings for this event
            // and displays the name of each user successfully booked for that event
            for (Booking b : ev.getConfirmedBookings()) {
                sb.append("   - ").append(b.getUser().getName())
                        .append(" ").append(b.getUser().getSurname())
                        .append("\n");
            }

            // Only displays the waitlist section if the event has users in the waitlist
            if (!ev.getWaitlist().isEmpty()) {
                sb.append("   Waitlist:\n");

                // Loops through all confirmed bookings currently on the waitlist
                // and displays the name of each user on the waitlist for that event
                for (Booking b : ev.getWaitlist()) {
                    sb.append("   - ").append(b.getUser().getName())
                            .append(" ").append(b.getUser().getSurname())
                            .append("\n");
                }
            }
        }

        eventOutput.setText(sb.toString()); // Displays the text in the event output area

        // Update the booking dropdowns so they match the latest data
        refreshBookingCombos();
    }


    // Refreshes the user display area with the latest user information
    // Refreshes the user display area with the latest user information
    private void refreshUsers() {
        StringBuilder sb = new StringBuilder();

        // Header row
        sb.append(String.format("%-10s %-15s %-15s %-38s %-15s %-10s%n",
                "ID", "Name", "Surname", "Email", "Birthdate", "Type"));

        // User rows
        for (User u : users) {
            sb.append(String.format("%-10d %-15s %-15s %-25s %-12s %-10s%n",
                    u.getID(),
                    u.getName(),
                    u.getSurname(),
                    u.getEmail(),
                    u.getBirthdate(),
                    u.getUserType()));
        }

        userOutput.setText(sb.toString());

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
                    .append(" (").append(b.getEvent().getTitle()).append(")")
                    .append(" | status=").append(b.getStatus())
                    .append(" | created=").append(b.getWhenCreated().format(DISPLAY_FMT))
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
            uList.add(u.getID() + " - " + u.getName() + " " + u.getSurname()
                    + " (" + u.getUserType() + ")");
        }

        // Replace the items inside the user comboBox
        bookingUserCombo.setItems(FXCollections.observableArrayList(uList));

        // Creates a list of event display strings for the event ComboBox
        List<String> evList = new ArrayList<>();
        for (Event ev : events) {
            evList.add(ev.getEventId() + " - " + ev.getTitle()
                    + " (" + ev.getDateTime().format(DISPLAY_FMT) + ")");
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


    // Sample data provided in the gui when the program starts
    private void seedSampleData() {
        // Add sample user
        try {
            users.add(new User("Aiden", "Gabriel", 8, 30, 2007, 138849, "agabriel@uoguelph.ca", UserType.STUDENT));
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

    // Creates a reusable header component (top bar + branding + navigation buttons)
    private VBox createHeader(String whiteTopBarStyle, String blackHeaderStyle, String navButtonStyle,
                              Button eventBtn, Button userBtn, Button waitlistBtn, Button bookingBtn,
                              Image logoImage) {

        // Top white bar (visual styling strip above header)
        Region whiteTopBar = new Region();
        whiteTopBar.setPrefHeight(26);
        whiteTopBar.setStyle(whiteTopBarStyle);

        // Logo image setup
        ImageView logoView = new ImageView(logoImage);
        logoView.setFitHeight(85); // Scale logo height
        logoView.setPreserveRatio(true); // Maintain aspect ratio
        logoView.setTranslateX(-75); // Shift logo left for alignment

        // University title text
        Label title = new Label("UNIVERSITY OF\nGUELPH");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 30px; -fx-font-weight: bold;");
        title.setTranslateX(-75); // Align text with logo

        // Decorative accent shapes (branding design)
        Polygon redAccent = new Polygon(-15,-10, 25,-10, 80,105, -15,105);
        redAccent.setFill(Color.web("#C20430")); // Red accent

        Polygon goldAccent = new Polygon(-15,-10, 70,-10, -15,105);
        goldAccent.setFill(Color.web("#FFC72C")); // Gold accent

        // Container for accent shapes
        Pane accentPane = new Pane();
        accentPane.setPrefSize(140, 95);
        accentPane.getChildren().addAll(goldAccent, redAccent);

        // Combine logo and title horizontally
        HBox logoAndTitleBox = new HBox(12, logoView, title);
        logoAndTitleBox.setAlignment(Pos.CENTER_LEFT);

        // Left side of header (branding + accents)
        HBox leftBranding = new HBox(18, accentPane, logoAndTitleBox);
        leftBranding.setPadding(new Insets(10, 20, 10, 12));
        leftBranding.setStyle(blackHeaderStyle);

        // Navigation buttons (new buttons created to avoid reusing original ones)
        Button eBtn = new Button("Events");
        Button uBtn = new Button("Users");
        Button wBtn = new Button("Waitlist");
        Button bBtn = new Button("Bookings");

        // Apply consistent navigation button styling
        eBtn.setStyle(navButtonStyle);
        uBtn.setStyle(navButtonStyle);
        wBtn.setStyle(navButtonStyle);
        bBtn.setStyle(navButtonStyle);

        // Copy functionality from original buttons to maintain navigation behavior
        eBtn.setOnAction(eventBtn.getOnAction());
        uBtn.setOnAction(userBtn.getOnAction());
        wBtn.setOnAction(waitlistBtn.getOnAction());
        bBtn.setOnAction(bookingBtn.getOnAction());

        // Navigation bar layout (centered buttons with spacing)
        HBox navBar = new HBox(45, eBtn, uBtn, wBtn, bBtn);
        navBar.setPadding(new Insets(0, 0, 0, 120));
        navBar.setStyle(blackHeaderStyle);
        navBar.setAlignment(Pos.CENTER);
        HBox.setHgrow(navBar, Priority.ALWAYS); // Allow nav bar to expand horizontally

        // Combine branding (left) and navigation (right)
        HBox headerBar = new HBox(leftBranding, navBar);
        headerBar.setStyle(blackHeaderStyle);

        // Return full header (white strip + main header bar)
        return new VBox(whiteTopBar, headerBar);
    }
}