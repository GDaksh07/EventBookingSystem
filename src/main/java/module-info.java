module com.example.oppprogrammingfinalrepo {
    requires javafx.controls;
    requires javafx.fxml;

    exports com.example.oppprogrammingfinalrepo;

    exports Managers;
    exports Event_Management;
    exports User_Management;
    exports Booking_Management;
    exports Waitlist_Management;
    exports enums;

    opens com.example.oppprogrammingfinalrepo to javafx.fxml;
}