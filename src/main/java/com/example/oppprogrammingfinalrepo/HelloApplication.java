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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Comparator;

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

    private int nextBookingId = 1; // Counter for auto-generating booking IDs

    // Date/time format used for parsing and display
    private static final DateTimeFormatter INPUT_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"); // Used for reading input from user
    private static final DateTimeFormatter DISPLAY_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"); // Used for showing in the UI

    // Manages the waitlist system for the events that are full
    private final WaitlistManager waitlistManager = new WaitlistManager();

    // UI controls referenced across methods
    // Displays the designated text in different spots of the program
    // Textboxes or Tableviews
    private TableView<Event> eventTable;
    private TableView<User> userTable;
    private TableView<Booking> bookingTable;
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
        // Styling logic done as: button colour, text fill colour, text size, bolded/normal, padding (space inside button)
        String pageBackgroundStyle = "-fx-background-color: #ECECEC;";
        String whiteTopBarStyle = "-fx-background-color: white;";
        String blackHeaderStyle = "-fx-background-color: black;";
        String navButtonStyle = "-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold; -fx-padding: 10 18 10 18;";
        String featuredCardStyle = "-fx-background-color: white; -fx-background-radius: 8; -fx-border-color: #D9D9D9; -fx-border-radius: 8;";
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
        featuredCard.setMaxWidth(1160); // Controls how wide the white box is
        featuredCard.setPrefWidth(900); // Controls how tall the white box is

        // Main vertical layout for the home screen
        // Layout is like the official website, white bar at the top, main header bar and added featured card
        VBox mainRoot = new VBox(0, whiteTopBar, headerBar, featuredCard);
        mainRoot.setStyle(pageBackgroundStyle); // Applies the overall page background styling
        mainRoot.setPadding(new Insets(0, 0, 30, 0));
        mainRoot.setAlignment(Pos.TOP_CENTER);
        featuredCard.setTranslateY(20); // Manually shifted card upwards

        // Creates the main menu screen to be wider like a website page
        mainScene = new Scene(mainRoot, 1200, 700);
        scenes[0] = mainScene;


        // Event Screen
        eventTable = new TableView<>(); // Creates a new tableview to display event objects

        // Create a column for Event ID
        TableColumn<Event, String> eventIdCol = new TableColumn<>("ID");
        // Define how to get the value for each cell (Event ID)
        eventIdCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getEventId()));

        // Create a column for Event Title
        TableColumn<Event, String> eventTitleCol = new TableColumn<>("Title");
        // Set how the title is retrieved for each row
        eventTitleCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getTitle()));

        // Create a column for Event Date
        TableColumn<Event, String> eventDateCol = new TableColumn<>("Date");
        // Format the LocalDateTime into a readable string using DISPLAY_FMT
        eventDateCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                data.getValue().getDateTime().format(DISPLAY_FMT)));

        // Create a column for Event Location
        TableColumn<Event, String> eventLocCol = new TableColumn<>("Location");
        // Retrieve the location value for each row
        eventLocCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getLocation()));

        // Create a column for Event Status (Active, Cancelled, etc.)
        TableColumn<Event, String> eventStatusCol = new TableColumn<>("Status");
        // Convert the status enum to a string for display
        eventStatusCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getStatus().toString()));

        // Create a column for Event type (Concert, Seminar, Workshop)
        TableColumn<Event, String> eventTypeCol = new TableColumn<>("Type");
        eventTypeCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getEventType().toString()));

        // Create a column for the Event Extras (Age restriction, Speaker, Topic)
        TableColumn<Event, String> eventExtraCol = new TableColumn<>("Extra");
        // Logic for retrieving the age restriction, speaker or topic
        eventExtraCol.setCellValueFactory(data -> {
            Event e = data.getValue(); // Get the Event object for the current row
            String str = ""; // Initialize a string to store what will be displayed in the column

            // Checks which event type it is to determine the label to give it
            // If age restriction, it gives a number
            // if speaker or workshop it gives a string
            if (e instanceof Concert c) {
                str = "Age: " + c.getAgeRestriction();
            } else if (e instanceof Seminar s) {
                str = "Speaker: " + s.getSpeakerName();
            } else if (e instanceof Workshop w) {
                str = "Topic: " + w.getTopic();
            }

            // Return the final value wrapped in a JavaFX property so it can be displayed in the table
            return new javafx.beans.property.SimpleStringProperty(str);
        });

        // Create a column for the capacity
        TableColumn<Event, String> eventCapacityCol = new TableColumn<>("Capacity");
        // Displays capacity in terms of how many are registers / total capacity
        eventCapacityCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getConfirmedBookings().size() + " / " +
                                data.getValue().getCapacity()
                )
        );

        // Add all columns to the table
        eventTable.getColumns().addAll(eventIdCol, eventTitleCol, eventDateCol, eventLocCol, eventCapacityCol, eventStatusCol, eventTypeCol, eventExtraCol);

        // Make columns automatically resize to fill available width
        eventTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        eventTable.setPrefHeight(240); // Sets preferred height of the table
        eventTable.setMaxWidth(1190); // Limits max width of the table

        eventTable.setEditable(false); // prevents the user from editing the text
        eventTable.setPrefHeight(220); // sets a fixed height for the output display area

        // Inputs
        // Creation Inputs
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

        // Extra field used for subclass constructors
        // For example, Concert requiring an age restriction
        TextField extraField = new TextField();
        extraField.setPromptText("Extra (age/topic/materials)");

        // Event selection
        // Dropdown menu for selecting each subclass type
        ComboBox<EventType> evType = new ComboBox<>();
        // Adds the set items into the dropdown menu
        evType.setItems(FXCollections.observableArrayList(EventType.CONCERT, EventType.SEMINAR, EventType.WORKSHOP));
        evType.setValue(EventType.CONCERT); // Sets the dropdown first to CONCERT

        // Search Inputs
        TextField searchField = new TextField(); // Text field for the title name
        searchField.setPromptText("Search by title");

        ComboBox<EventType> filterType = new ComboBox<>(); // Dropdown for searching for a event type in general
        filterType.getItems().addAll(EventType.CONCERT, EventType.SEMINAR, EventType.WORKSHOP); // Adds the types
        filterType.setPromptText("Filter by type");

        // Adds buttons for the search fields
        Button searchBtn = new Button("Search / Filter");
        Button clearSearchBtn = new Button("Clear");

        // Canceling an Event
        TextField cancelEventId = new TextField(); // Field where the user enters an ID to the event they want to cancel
        cancelEventId.setPromptText("Event ID to cancel");

        // Updating an event
        TextField updateTitle = new TextField(); // Text field for the new title name
        updateTitle.setPromptText("New title");

        TextField updateDate = new TextField(); // Text field for the new date
        updateDate.setPromptText("New DateTime (yyyy-MM-dd HH:mm)");

        TextField updateLocation = new TextField(); // Text field for the new location
        updateLocation.setPromptText("New location");

        TextField updateCapacity = new TextField(); // Text field for the new capacity
        updateCapacity.setPromptText("New capacity");

        TextField updateExtra = new TextField(); // Text field for the new extra field
        updateExtra.setPromptText("New extra (age/topic/speaker)");

        // Buttons
        Button createEventBtn = new Button("Create Event");
        Button refreshEventsBtn = new Button("Refresh List");
        Button cancelEventBtn = new Button("Cancel Event");
        Button backFromEvent = new Button("Back");
        Button updateEventBtn = new Button("Update Event");
        Button clearUpdateBtn = new Button("Clear");

        // First row of the event containing basic event details
        HBox evFormRow1 = new HBox(8, evTitle, evDate, evLocation, evCapacity, evType, extraField);
        evFormRow1.setAlignment(Pos.CENTER);

        // Second row for different controls like creating the event, refresh button ,canceling or searching
        HBox evFormRow2 = new HBox(8, createEventBtn, refreshEventsBtn, cancelEventId, cancelEventBtn, searchField, filterType, searchBtn, clearSearchBtn);
        evFormRow2.setAlignment(Pos.CENTER);

        HBox evUpdateRow = new HBox(8, updateTitle, updateDate, updateLocation, updateCapacity, updateExtra, updateEventBtn, clearUpdateBtn);
        evUpdateRow.setAlignment(Pos.CENTER);

        // Creates vertical layout for the event rows
        VBox eventFormSection = new VBox(10, evFormRow1, evFormRow2, evUpdateRow);
        eventFormSection.setPadding(new Insets(15)); // Adds padding
        eventFormSection.setMaxWidth(1190); // Sets the maximum width of the form section
        // Applies styling to the boxes
        eventFormSection.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: #D9D9D9;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;"
        );

        // Builds the full event management screen layout
        // Stacks the specific items, like HBox and labels
        Label eventHeader = new Label("Event Management"); // Section header label displayed at the top of the Event Management screen

        // Apply styling to the header text to make it stand out
        // Uses a larger font size, bold weight, and the university red color for consistency
        eventHeader.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #C20430;");

        Label eventMessage = new Label(); // Creates a label to display error messages
        eventMessage.setStyle("-fx-text-fill: red; -fx-font-size: 13px;"); // Adds styling

        // Create the top header section (navigation bar with buttons and logo)
        VBox eventHeaderTop = createHeader(whiteTopBarStyle, blackHeaderStyle, navButtonStyle, eventBtn, userBtn, waitlistBtn, bookingBtn, logoImage);

        // Main content layout for Event Management screen (form inputs + output display)
        VBox eventContent = new VBox(15, eventHeader, eventTable, eventMessage, eventFormSection, backFromEvent);
        eventContent.setAlignment(Pos.TOP_CENTER);
        eventContent.setPadding(new Insets(12));

        // Root layout combining header and content vertically
        // Creates a scroll panel to allow scrolling for the eventContent layout
        ScrollPane eventScroll = new ScrollPane(eventContent);
        eventScroll.setFitToWidth(true);

        // Shows horizontal and vertical scroller if needed (not needed if there is enough spots on the screen)
        eventScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        eventScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        // Creates the main root layout
        VBox eventRoot = new VBox(0, eventHeaderTop, eventScroll);

        eventRoot.setStyle(pageBackgroundStyle); // Apply background styling to entire screen

        // Create the Event Management scene and assign it to index 1 in the scenes array
        eventScene = new Scene(eventRoot, 1200, 700);
        scenes[1] = eventScene;


        // User Screen
        userTable = new TableView<>(); // Creates a table view to display user objects

        // Column for User ID
        TableColumn<User, String> userIdCol = new TableColumn<>("ID");
        // Convert the ID (likely an int) to a String for display
        userIdCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(String.valueOf(data.getValue().getID())));

        // Column for User First Name
        TableColumn<User, String> userNameCol = new TableColumn<>("Name");
        // Get the user's name for each row
        userNameCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getName()));

        // Column for User Surname
        TableColumn<User, String> userSurnameCol = new TableColumn<>("Surname");
        // Get the user's surname for each row
        userSurnameCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getSurname()));

        // Column for User Email
        TableColumn<User, String> userEmailCol = new TableColumn<>("Email");
        // Get the user's email for display
        userEmailCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getEmail()));

        // Column for User Birthdate
        TableColumn<User, String> userBirthCol = new TableColumn<>("Birthdate");
        // Display the birthdate (already formatted as a String)
        userBirthCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getBirthdate()));

        // Column for User Type
        TableColumn<User, String> userTypeCol = new TableColumn<>("Type");
        // Convert enum to String for display
        userTypeCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getUserType().toString()));

        // Add all columns to the table
        userTable.getColumns().addAll(userIdCol, userNameCol, userSurnameCol, userEmailCol, userBirthCol, userTypeCol);

        // Make columns automatically resize to fill available width
        userTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        userTable.setPrefHeight(240); // Sets preferred height
        userTable.setMaxWidth(1190); // Sets max width

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

        // Create a TextArea to display user information
        TextArea userOutput = new TextArea();
        userOutput.setEditable(false);
        userOutput.setPrefHeight(200); // Sets the height of the text area to show the content

        // Buttons
        Button addUserBtn = new Button("Add User");
        Button refreshUsersBtn = new Button("Refresh Users");
        Button removeUserBtn = new Button("Remove User");
        Button backFromUser = new Button("Back");
        Button viewUserBtn = new Button("View User Details");

        // First horizontal row containing all user input fields
        HBox userFormRow = new HBox(10, userName, userSurname, userEmail, userMonth, userDay, userYear, userIdField, userTypeLabel, userTypeCombo);
        userFormRow.setAlignment(Pos.CENTER);
        userFormRow.setSpacing(10);

        // Second horizontal row containing action buttons and removal field
        HBox userFormRow2 = new HBox(8, addUserBtn, refreshUsersBtn, viewUserId, viewUserBtn, removeUserId, removeUserBtn);
        userFormRow2.setAlignment(Pos.CENTER);
        userFormRow2.setSpacing(10);

        // Create a vertical layout to group both form rows together
        VBox userFormSection = new VBox(10, userFormRow, userFormRow2);
        userFormSection.setPadding(new Insets(15));
        userFormSection.setMaxWidth(1190); // Sets a max width for the section
        // Adds styling
        userFormSection.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: #D9D9D9;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;"
        );

        // Vertical layout that builds the full User Management screen
        Label userHeader = new Label("User Management"); // Section header label displayed at the top of the User Management screen

        Label userMessage = new Label(); // Label for displaying different messages from error messages to succession messages for the user section
        userMessage.setStyle("-fx-text-fill: red; -fx-font-size: 13px;");

        // Apply styling to the header text to make it stand out
        // Uses a larger font size, bold weight, and the university red color for consistency
        userHeader.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #C20430;");

        // Create the top header section (navigation bar with buttons and logo)
        VBox userHeaderTop = createHeader(whiteTopBarStyle, blackHeaderStyle, navButtonStyle, eventBtn, userBtn, waitlistBtn, bookingBtn, logoImage);

        // Main content layout for User Management screen (user form inputs + output display)
        VBox userContent = new VBox(15, userHeader, userTable, userMessage, userFormSection, backFromUser, userOutput);
        userContent.setAlignment(Pos.TOP_CENTER);
        userContent.setPadding(new Insets(12)); // Padding - 12 pixels of whitespace on all sides so it doesn't touch edges

        // Create a ScrollPane to allow scrolling for the userContent layout
        ScrollPane userScroll = new ScrollPane(userContent);
        userScroll.setFitToWidth(true);

        // Uses horizontal and vertical scrolling if needed
        userScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        userScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        // Root layout combining header and content vertically
        VBox userRoot = new VBox(0, userHeaderTop, userScroll);

        userRoot.setStyle(pageBackgroundStyle); // Applies the overall page background style used across the application

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
        waitContent.setAlignment(Pos.TOP_CENTER);

        // Root layout combining header and content vertically
        VBox waitRoot = new VBox(0, waitHeaderTop, waitContent);
        waitRoot.setStyle(pageBackgroundStyle); // Applies the overall page background style used across the application

        // Create the Waitlist scene and assign it to index 3 in the scenes array
        waitScene = new Scene(waitRoot, 1200, 700);
        scenes[3] = waitScene;


        // Booking Screen
        // Create a TableView to display Booking objects in a structured table format
        bookingTable = new TableView<>();

        // Create a column to display the booking ID
        TableColumn<Booking, String> bookingIdCol = new TableColumn<>("Booking ID");

        // Define how to extract the booking ID from each Booking object
        bookingIdCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getBookingId()));

        // Create a column to display the user ID associated with the booking
        TableColumn<Booking, String> userCol = new TableColumn<>("User ID");

        // Extracts the user ID from the Booking object and convert it to a String
        userCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(String.valueOf(data.getValue().getUser().getID())));

        // Create a column to display the event ID for the booking
        TableColumn<Booking, String> eventCol = new TableColumn<>("Event ID");

        // Extracts the event ID from the Booking object
        eventCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getEvent().getEventId()));

        // Create a column to display the event title
        TableColumn<Booking, String> titleCol = new TableColumn<>("Event Title");

        // Extracts the event title from the Booking object
        titleCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getEvent().getTitle()));

        // Create a column to display the booking status (CONFIRMED, WAITLISTED, CANCELLED)
        TableColumn<Booking, String> statusCol = new TableColumn<>("Status");

        // Convert the booking status enum to a String for display
        statusCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getStatus().toString()));

        // Create a column to display when the booking was created
        TableColumn<Booking, String> timeCol = new TableColumn<>("Created");

        // Format the booking creation time into a readable string using DISPLAY_FMT
        timeCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getWhenCreated().format(DISPLAY_FMT)));

        // Add all created columns to the TableView so they appear in the UI
        bookingTable.getColumns().addAll(bookingIdCol, userCol, eventCol, titleCol, statusCol, timeCol);

        bookingTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY); // Make columns automatically resize to fill the available table width
        bookingTable.setPrefHeight(240); // Set the preferred height of the table
        bookingTable.setMaxWidth(1190); // Set the maximum width so it aligns with other UI sections

        // Dropdown menu for selecting a user when creating a booking
        bookingUserCombo = new ComboBox<>();
        bookingUserCombo.setPromptText("Select user (ID - name)");

        // Dropdown menu for selecting
        bookingEventCombo = new ComboBox<>();
        bookingEventCombo.setPromptText("Select event (ID - title)");

        TextField cancelBookingId = new TextField(); // Text field for where the user enters the booking ID to cancel
        cancelBookingId.setPromptText("Booking ID to cancel");

        // Buttons
        Button createBookingBtn = new Button("Create Booking");
        Button refreshBookingsBtn = new Button("Refresh Bookings");
        Button cancelBookingBtn = new Button("Cancel Booking");
        Button backFromBooking = new Button("Back");

        // First row contains the booking creation inputs
        HBox bookingRow1 = new HBox(8, bookingUserCombo, bookingEventCombo);
        bookingRow1.setAlignment(Pos.CENTER);
        bookingRow1.setSpacing(10);

        // Second row contains booking action buttons and cancellation input
        HBox bookingRow2 = new HBox(8, createBookingBtn, refreshBookingsBtn, cancelBookingId, cancelBookingBtn);
        bookingRow2.setAlignment(Pos.CENTER);
        bookingRow2.setSpacing(10);

        // Main vertical layout for the Booking Management screen
        Label bookingHeader = new Label("Booking Management"); // Section header label displayed at the top of the Booking Management screen

        Label bookingMessage = new Label(); // Label for displaying different messages from error messages to succession messages for the user section
        bookingMessage.setStyle("-fx-text-fill: red; -fx-font-size: 13px;");

        // Apply styling to the header text to make it stand out
        // Uses a larger font size, bold weight, and the university red color for consistency
        bookingHeader.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #C20430;");

        // Create the top header section (navigation bar with buttons and logo)
        VBox bookingHeaderTop = createHeader(whiteTopBarStyle, blackHeaderStyle, navButtonStyle, eventBtn, userBtn, waitlistBtn, bookingBtn, logoImage);

        // Vertical container to hold booking input fields and action buttons
        VBox bookingFormSection = new VBox(10, bookingRow1, bookingRow2);
        bookingFormSection.setPadding(new Insets(15));
        bookingFormSection.setMaxWidth(1190);
        // Apply styling to make the form look like a white card with a border
        bookingFormSection.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: #D9D9D9;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;"
        );

        // Main content layout for Booking Management screen (booking form inputs + output display)
        VBox bookingContent = new VBox(15, bookingHeader, bookingTable, bookingMessage, bookingFormSection, backFromBooking);
        bookingContent.setAlignment(Pos.TOP_CENTER);
        bookingContent.setPadding(new Insets(12)); // Adds padding around the layout

        // Create a ScrollPane to allow scrolling for the userContent layout
        ScrollPane bookingScroll = new ScrollPane(bookingContent);
        bookingScroll.setFitToWidth(true);

        // Uses horizontal and vertical scrolling if needed
        bookingScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        bookingScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        // Root layout combining header and content vertically
        VBox bookingRoot = new VBox(0, bookingHeaderTop, bookingScroll);
        bookingRoot.setStyle(pageBackgroundStyle); // Applies the overall page background style used across the application

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
                        String topic = extraField.getText().trim().isEmpty() ? "TBA" : extraField.getText().trim();
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

                saveData(); // Uses saveData() to save a created event

                eventMessage.setText("Event created successfully."); // Displays a success message if event meets criteria

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
                eventMessage.setText("Error: Check inputs (DateTime format: yyyy-MM-dd HH:mm) or check if all fields are filled");
            }
        });

        // Refreshes the event display when the "Refresh List" button is clicked
        refreshEventsBtn.setOnAction(e -> refreshEvents());

        // Runs when the user clicks the "Cancel Event" button
        cancelEventBtn.setOnAction(e -> {
            String id = cancelEventId.getText().trim(); // Reads the event ID entered by the user

            // Validates the ID was entered
            if (id.isEmpty()) {
                eventMessage.setText("Enter Event ID to cancel.");
                return;
            }

            Event found = findEventById(id); // Finds the matching event object

            // Shows an error message if event does not exist
            if (found == null) {
                eventMessage.setText("Event not found: " + id);
                return;
            }

            try {
                found.cancelEvent(); // Mark the event as cancelled

                // Let the waitlist manager handle cancellation related logic
                waitlistManager.handleEventCancelled(found);

                for (Booking b : bookings.values()) {
                    if (b.getEvent().equals(found)) {
                        b.setStatus(BookingStatus.CANCELLED);
                    }
                }

                saveData(); // Uses saveData() to save a cancelled event

                eventMessage.setText("Event cancelled successfully."); // Displays a success message if cancellation meets criteria

                refreshEvents(); // Refresh the event display again

                // Shows an error message if the cancellation fails
            } catch (Exception ex) {
                eventMessage.setText("CANCEL ERROR: " + ex.getMessage());
            }
        });

        // Runs when the user clicks the "Search / Filter" button
        searchBtn.setOnAction(e -> {
            // Get the search text, remove extra spaces, and convert to lowercase for case-insensitive matching
            String searchText = searchField.getText().trim().toLowerCase();

            // Get the selected event type from the dropdown (can be null if no filter is selected)
            EventType selectedType = filterType.getValue();

            // Create a list to store events that match the search/filter criteria
            List<Event> filtered = new ArrayList<>();

            // Loop through all events in the system
            for (Event ev : events) {

                // Check if event title matches the search text (or allow all if search is empty)
                boolean matchesTitle = searchText.isEmpty() ||
                        ev.getTitle().toLowerCase().contains(searchText);

                // Check if event type matches the selected filter (or allow all if no filter is selected)
                boolean matchesType = (selectedType == null) ||
                        ev.getEventType() == selectedType;

                // Add event to filtered list only if both conditions are true
                if (matchesTitle && matchesType) {
                    filtered.add(ev);
                }
            }

            // Display the filtered list in the table
            eventTable.setItems(FXCollections.observableArrayList(filtered));
        });


        // Runs when the user clicks the "Update Event" button
        updateEventBtn.setOnAction(e -> {
            try {
                // Get the currently selected event from the table
                Event ev = eventTable.getSelectionModel().getSelectedItem();

                // If no event is selected, show error message
                if (ev == null) {
                    eventMessage.setText("Select an event from the table.");
                    return;
                }

                // Update title if a new value was entered
                if (!updateTitle.getText().trim().isEmpty()) {
                    ev.setTitle(updateTitle.getText().trim());
                }

                // Update date/time if a new value was entered
                if (!updateDate.getText().trim().isEmpty()) {
                    ev.setDateTime(LocalDateTime.parse(updateDate.getText().trim(), INPUT_FMT));
                }

                // Update location if a new value was entered
                if (!updateLocation.getText().trim().isEmpty()) {
                    ev.setLocation(updateLocation.getText().trim());
                }

                // Update capacity if a new value was entered
                if (!updateCapacity.getText().trim().isEmpty()) {
                    ev.setCapacity(Integer.parseInt(updateCapacity.getText().trim()));
                }

                // Update extra field depending on event type
                if (!updateExtra.getText().trim().isEmpty()) {
                    if (ev instanceof Concert c) {
                        c.setAgeRestriction(Integer.parseInt(updateExtra.getText().trim()));
                    } else if (ev instanceof Seminar s) {
                        s.setSpeakerName(updateExtra.getText().trim());
                    } else if (ev instanceof Workshop w) {
                        w.setTopic(updateExtra.getText().trim());
                    }
                }

                // Refresh table to reflect updated data
                refreshEvents();

                saveData(); // Uses saveData() to save a updated event

                // Show success message
                eventMessage.setText("Event updated successfully.");

            } catch (Exception ex) {
                // Show error message if update fails
                eventMessage.setText("Update failed: " + ex.getMessage());
            }
        });


        // Runs when the user clicks on a row in the event table
        eventTable.setOnMouseClicked(e -> {
            // Get the selected event
            Event ev = eventTable.getSelectionModel().getSelectedItem();

            // If an event is selected, populate update fields with current values
            if (ev != null) {

                // Fill text fields with existing event data
                updateTitle.setText(ev.getTitle());
                updateDate.setText(ev.getDateTime().format(INPUT_FMT));
                updateLocation.setText(ev.getLocation());
                updateCapacity.setText(String.valueOf(ev.getCapacity()));

                // Fill extra field based on event type
                if (ev instanceof Concert c) {
                    updateExtra.setText(String.valueOf(c.getAgeRestriction()));
                } else if (ev instanceof Seminar s) {
                    updateExtra.setText(s.getSpeakerName());
                } else if (ev instanceof Workshop w) {
                    updateExtra.setText(w.getTopic());
                }
            }
        });


        // Runs when the user clicks the "Clear" search button
        clearSearchBtn.setOnAction(e -> {
            // Clear the search text field
            searchField.clear();

            // Reset the filter dropdown
            filterType.setValue(null);

            // Reload all events (remove filtering)
            refreshEvents();
        });


        // Runs when the user clicks the "Clear" update button
        clearUpdateBtn.setOnAction(e -> {
            // Clear all update input fields
            updateTitle.clear();
            updateDate.clear();
            updateLocation.clear();
            updateCapacity.clear();
            updateExtra.clear();

            // Clear the selected row in the table
            eventTable.getSelectionModel().clearSelection();

            // Clear any message shown to the user
            eventMessage.setText("");
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
                    userMessage.setText("User ID already exists.");
                    return;
                }

                // Creates a new user object using the entered information
                User u = new User(n, s, m, d, y, id, email, type);

                users.add(u); // Adds the users to the main user list

                saveData(); // Uses saveData() to save a created user

                userMessage.setText("User added successfully."); // Displays a success message if user meets criteria

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
                userMessage.setText("ADD USER ERROR: " + ex.getClass().getSimpleName() + " - " + ex.getMessage());
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
                    userMessage.setText("User not found: " + id);
                    return;
                }

                users.remove(found); // Removes the user from the user list

                // remove bookings already happens below
                bookings.values().removeIf(b -> b.getUser().equals(found));

                saveData(); // Uses saveData() to save a deleted user

                userMessage.setText("User removed successfully."); // Displays a success message if user meets criteria to be removed

                // Also remove bookings belonging to this user
                bookings.values().removeIf(b -> b.getUser().equals(found));

                refreshUsers(); // Refreshes the user display after removal

                // Shows an error message if removal fails
            } catch (Exception ex) {
                userMessage.setText("REMOVE USER ERROR: " + ex.getMessage());
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
                userOutput.setText("Invalid ID input."); // Handles invalid input
            }
        });


        // Actions: BOOKINGS
        // Runs when user clicks the "Create Booking" button
        createBookingBtn.setOnAction(e -> {
            try {
                // Reads the booking id and selected entries from the gui
                String bookingId = "B" + nextBookingId++;
                String userEntry = bookingUserCombo.getValue();
                String eventEntry = bookingEventCombo.getValue();

                // Checks dropdown selections if they are filled
                if (userEntry == null || eventEntry == null) {
                    bookingMessage.setText("Please select both a user and an event.");
                    return;
                }

                // Gets the user ID and eventID from the combobox display
                int userId = Integer.parseInt(userEntry.split(" - ")[0]);
                String eventId = eventEntry.split(" - ")[0];

                // Finds actual user and event objects using their IDs
                User u = findUserById(userId);
                Event ev = findEventById(eventId);

                // Stops if either user or event does not exist
                if (u == null) {
                    bookingMessage.setText("User not found.");
                    return;
                }
                if (ev == null) {
                    bookingMessage.setText("Event not found.");
                    return;
                }

                int activeBookings = 0;

                // Loops through to see if the booking belongs to a specific user and the booking was not canceled
                for (Booking b : bookings.values()) {
                    if (b.getUser().equals(u) && b.getStatus() != BookingStatus.CANCELLED) {
                        activeBookings++;
                    }
                }

                // Set limits
                int limit;
                switch (u.getUserType()) {
                    case STUDENT -> limit = 3;
                    case STAFF -> limit = 5;
                    case GUEST -> limit = 1;
                    default -> limit = 1;
                }

                // Enforces limit
                if (activeBookings >= limit) {
                    bookingMessage.setText("Booking limit reached for " + u.getUserType());
                    return;
                }

                // Only active events can be booked
                if (ev.getStatus() != enums.EventStatus.ACTIVE) {
                    bookingMessage.setText("Event is not active.");
                    return;
                }

                // Prevents the same user from booking the same event more than 1
                // unless if their previous booking was cancelled
                for (Booking b : bookings.values()) {
                    if (b.getUser().equals(u) && b.getEvent().equals(ev) && b.getStatus() != BookingStatus.CANCELLED) {
                        bookingMessage.setText("User already booked this event.");
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

                saveData(); // Uses saveData() to save a created booking

                bookingMessage.setText("Booking created successfully."); // Displays a success message if booking meets criteria

                // Refreshes the booking and event displays
                refreshBookings();
                refreshEvents();

                // Shows an error message if booking fails
            } catch (Exception ex) {
                bookingMessage.setText("CREATE BOOKING ERROR: " + ex.getClass().getSimpleName() + " - " + ex.getMessage());
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
                bookingMessage.setText("Enter booking ID to cancel.");
                return;
            }

            // lookup the booking id in the bookings map
            Booking b = bookings.get(id);

            // Error message if booking is not found
            if (b == null) {
                bookingMessage.setText("Booking not found: " + id);
                return;
            }

            // Error message if the booking is already cancelled
            if (b.getStatus() == BookingStatus.CANCELLED) {
                bookingMessage.setText("Already cancelled.");
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

            saveData(); // Uses saveData() to save a cancelled booking

            bookingMessage.setText("Booking cancelled successfully."); // Displays a success message if booking cancellation meets criteria
        });

        loadInitialData();

        // Configure and show the main application window
        stage.setTitle("OPP Final Project");
        stage.setScene(mainScene);
        stage.show();
    }


    // Helper methods

    // Outputs a refreshed version of the events screen
    private void refreshEvents() {
        // Sort events by date and time (earliest to latest)
        events.sort(Comparator.comparing(Event::getDateTime));

        // Convert the events list into an observable list and set it to the table
        // This allows the TableView to properly track and display updates
        eventTable.setItems(FXCollections.observableArrayList(events));
        eventTable.refresh(); // Refreshes the table to show all event changes

        refreshBookingCombos(); // Update booking-related dropdowns (since events may have changed)
    }


    // Refreshes the user display area with the latest user information
    private void refreshUsers() {
        // Set updated user list into the table (converted to observable list)
        userTable.setItems(FXCollections.observableArrayList(users));
        refreshBookingCombos(); // Update booking-related dropdowns (users may affect booking selections)
    }


    // Refreshes the booking display area with the latest booking information
    private void refreshBookings() {
        // Convert the bookings map values into an observable list and set it as the table data
        // This allows the TableView to display all current bookings
        bookingTable.setItems(FXCollections.observableArrayList(bookings.values()));

        bookingTable.refresh(); // Refreshes table to display updated screen
        refreshBookingCombos(); // Refreshes dropdowns in case they changed
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


    // Format used in CSV (matches project file format)
    private static final DateTimeFormatter CSV_DATE_TIME_FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    // Loads all data at startup (users, events, bookings)
    private void loadInitialData() {

        // Clear existing data before loading fresh data
        users.clear();
        events.clear();
        bookings.clear();

        try {
            // Load each file
            loadUsersFromCsv("users.csv");
            loadEventsFromCsv("events.csv");
            loadBookingsFromCsv("bookings.csv");

            // Refresh GUI so data shows up
            refreshUsers();
            refreshEvents();
            refreshBookings();

        } catch (Exception e) {
            System.out.println("LOAD ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }


    // Opens a file from the resources folder
    private BufferedReader openFile(String path) throws IOException {
        return new BufferedReader(new java.io.FileReader(path));
    }


    // USERS
    private void loadUsersFromCsv(String path) throws IOException {

        try (BufferedReader br = openFile(path)) {

            br.readLine(); // skip header row

            String line;
            while ((line = br.readLine()) != null) {

                if (line.trim().isEmpty()) continue;

                String[] p = line.split(",", -1);

                String idRaw = p[0].trim();
                String fullName = p[1].trim();
                String email = p[2].trim();
                String typeRaw = p[3].trim();

                // Extract number from ID (handles U001 → 1)
                int id = Integer.parseInt(idRaw.replaceAll("\\D+", ""));

                // Split full name into first + last
                String[] nameParts = fullName.split("\\s+", 2);
                String first = nameParts.length > 0 ? nameParts[0] : "Unknown";
                String last = nameParts.length > 1 ? nameParts[1] : "Unknown";

                // Convert string to enum
                UserType type = switch (typeRaw.toUpperCase()) {
                    case "STUDENT" -> UserType.STUDENT;
                    case "STAFF" -> UserType.STAFF;
                    case "GUEST" -> UserType.GUEST;
                    default -> throw new IllegalArgumentException("Invalid userType");
                };

                // Create user (dummy birthdate since class requires it)
                User u = new User(first, last, 1, 1, 2000, id, email, type);

                users.add(u);
            }
        }
    }


    // EVENTS
    private void loadEventsFromCsv(String path) throws IOException {

        try (BufferedReader br = openFile(path)) {

            br.readLine(); // skip header row

            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                String[] p = line.split(",", -1);

                String eventId = p[0].trim();
                String title = p[1].trim();
                LocalDateTime dt = LocalDateTime.parse(p[2].trim(), CSV_DATE_TIME_FMT);
                String location = p[3].trim();

                int capacity;
                try {
                    capacity = Integer.parseInt(p[4].trim());
                    if (capacity <= 0) capacity = 1;
                } catch (Exception e) {
                    capacity = 1; // fallback
                }

                String status = p[5].trim();
                String type = p[6].trim();
                String topic = p[7].trim();
                String speaker = p[8].trim();
                String ageRaw = p[9].trim();

                Event event;

                // Create correct event type based on CSV
                switch (type.toUpperCase()) {
                    case "WORKSHOP" ->
                            event = new Workshop(eventId, title, dt, location, capacity, topic);
                    case "SEMINAR" ->
                            event = new Seminar(eventId, title, dt, location, capacity, speaker);
                    case "CONCERT" -> {
                        int age;
                        try {
                            age = Integer.parseInt(ageRaw.replaceAll("\\D+", ""));
                        } catch (Exception e) {
                            age = 0; // default for "all ages"
                        }

                        event = new Concert(eventId, title, dt, location, capacity, age);
                    }
                    default -> throw new IllegalArgumentException("Invalid eventType");
                }

                // If event was cancelled in file, restore it
                if (status.trim().equalsIgnoreCase("Cancelled")) {
                    event.cancelEvent();
                }

                events.add(event);
            }
        }
    }


    // BOOKINGS
    private void loadBookingsFromCsv(String path) throws IOException {

        // Temporary class to store each row before processing
        class Row {
            String id;
            int userId;
            String eventId;
            LocalDateTime time;
            BookingStatus status;

            Row(String id, int userId, String eventId, LocalDateTime time, BookingStatus status) {
                this.id = id;
                this.userId = userId;
                this.eventId = eventId;
                this.time = time;
                this.status = status;
            }
        }

        List<Row> rows = new ArrayList<>();

        try (BufferedReader br = openFile(path)) {

            br.readLine(); // skip header

            String line;
            while ((line = br.readLine()) != null) {

                if (line.trim().isEmpty()) continue;

                String[] p = line.split(",", -1);

                // Extract booking data
                String id = p[0].trim();
                int userId = Integer.parseInt(p[1].trim().replaceAll("\\D+", ""));
                String eventId = p[2].trim();
                LocalDateTime time = LocalDateTime.parse(p[3].trim(), CSV_DATE_TIME_FMT);

                // Convert string to booking status
                BookingStatus status = switch (p[4].trim().toUpperCase()) {
                    case "CONFIRMED" -> BookingStatus.CONFIRMED;
                    case "WAITLISTED" -> BookingStatus.WAITLISTED;
                    case "CANCELLED" -> BookingStatus.CANCELLED;
                    default -> throw new IllegalArgumentException("Invalid bookingStatus");
                };

                rows.add(new Row(id, userId, eventId, time, status));
            }
        }

        // Sort bookings by time to keep correct order
        rows.sort(Comparator.comparing(r -> r.time));

        // Create actual booking objects
        for (Row r : rows) {

            User user = findUserById(r.userId);
            Event event = findEventById(r.eventId);

            if (user == null || event == null) continue;

            // Create booking using correct timestamp
            Booking b = new Booking(r.id, user, event, r.time, r.status);
            bookings.put(r.id, b);

            // Rebuild confirmed and waitlist states
            if (r.status == BookingStatus.CONFIRMED) {
                event.addConfirmedBooking(b);
                waitlistManager.addToConfirmed(event, user);

            } else if (r.status == BookingStatus.WAITLISTED) {
                event.addToWaitlist(b);
                waitlistManager.addToWaitlist(event, user);
            }
        }
    }


    // Loops through all save methods and essentially checks if anything was changed
    // For example if users were not changed and since it loops through all users, it will go to the next method
    // Once in the next method of saveEvents() it will loop through and if something was changed it will be saved
    // Essentially looping through all methods to make sure all files are always up to date
    private void saveData() {
        saveUsers(); // utilizes the saveUsers function to save the data in the correct csv file
        saveEvents(); // utilizes the saveEvents function to save the data in the correct csv file
        saveBookings(); // utilizes the saveBookings function to save the data in the correct csv file
    }


    // Saves all users from memory into users.csv
    // Each user is written in CSV format matching the load method structure
    private void saveUsers() {
        try (PrintWriter writer = new PrintWriter("users.csv")) {
            writer.println("id,name,email,type"); // Write CSV header row (required for proper loading)

            // Loop through all users and write their data
            for (User u : users) {
                writer.println("U" + u.getID() + "," +
                        u.getName() + " " + u.getSurname() + "," +
                        u.getEmail() + "," +
                        u.getUserType());
            }

        } catch (Exception e) {
            System.out.println("SAVE USERS ERROR: " + e.getMessage()); // Handles file writing errors
        }
    }


    // Saves all events into events.csv
    // Includes extra fields depending on event type (Workshop, Seminar, Concert)
    private void saveEvents() {
        try (PrintWriter writer = new PrintWriter("events.csv")) {
            // Write CSV header row
            writer.println("eventId,title,dateTime,location,capacity,status,type,topic,speaker,age");

            // Loop through all events
            for (Event e : events) {
                // Default empty values for optional fields
                String topic = "";
                String speaker = "";
                String age = "";

                // Fill only the relevant field depending on event subclass
                if (e instanceof Workshop w) topic = w.getTopic();
                if (e instanceof Seminar s) speaker = s.getSpeakerName();
                if (e instanceof Concert c) age = String.valueOf(c.getAgeRestriction());

                // Write event data in correct CSV format
                writer.println(e.getEventId() + "," +
                        e.getTitle() + "," +
                        e.getDateTime().format(CSV_DATE_TIME_FMT) + "," +
                        e.getLocation() + "," +
                        e.getCapacity() + "," +
                        e.getStatus() + "," +
                        e.getEventType() + "," +
                        topic + "," +
                        speaker + "," +
                        age);
            }

        } catch (Exception e) {
            System.out.println("SAVE EVENTS ERROR: " + e.getMessage()); // Handles file writing errors
        }
    }


    // Saves all bookings into bookings.csv
    // Maintains relationships between users and events
    private void saveBookings() {
        try (PrintWriter writer = new PrintWriter("bookings.csv")) {
            // Write CSV header row
            writer.println("bookingId,userId,eventId,time,status");

            // Loop through all bookings in the system
            for (Booking b : bookings.values()) {
                writer.println(b.getBookingId() + "," +
                        "U" + b.getUser().getID() + "," +
                        b.getEvent().getEventId() + "," +
                        b.getWhenCreated().format(CSV_DATE_TIME_FMT) + "," +
                        b.getStatus());
            }

        } catch (Exception e) {
            System.out.println("SAVE BOOKINGS ERROR: " + e.getMessage()); // Handles file writing errors
        }
    }
}