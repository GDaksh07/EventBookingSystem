package Booking_Management;

import Event_Management.Event;
import User_Management.User;
import enums.BookingStatus;

import java.time.LocalDateTime;

public class Booking {

    // Unique booking ID
    private final String bookingId;

    // User who made the booking
    private final User user;

    // Event the booking is for
    private final Event event;

    // Time the booking was created
    private final LocalDateTime whenCreated;

    // Current status of the booking
    private BookingStatus status;

    // Constructor used when creating a booking normally
    // Sets the time to the current moment
    public Booking(String bookingId, User user, Event event, BookingStatus status) {
        this(bookingId, user, event, LocalDateTime.now(), status);
    }

    // Constructor that allows setting a specific creation time
    public Booking(String bookingId, User user, Event event, LocalDateTime whenCreated, BookingStatus status) {

        // Make sure all required values are provided
        if (bookingId == null || bookingId.isBlank()) {
            throw new IllegalArgumentException("bookingId required");
        }
        if (user == null) {
            throw new IllegalArgumentException("user required");
        }
        if (event == null) {
            throw new IllegalArgumentException("event required");
        }
        if (whenCreated == null) {
            throw new IllegalArgumentException("whenCreated required");
        }
        if (status == null) {
            throw new IllegalArgumentException("status required");
        }

        // Assign values to this booking
        this.bookingId = bookingId;
        this.user = user;
        this.event = event;
        this.whenCreated = whenCreated;
        this.status = status;
    }

    // Returns booking ID
    public String getBookingId() {
        return bookingId;
    }

    // Returns user
    public User getUser() {
        return user;
    }

    // Returns event
    public Event getEvent() {
        return event;
    }

    // Returns creation time
    public LocalDateTime getWhenCreated() {
        return whenCreated;
    }

    // Returns current status
    public BookingStatus getStatus() {
        return status;
    }

    // Updates booking status
    public void setStatus(BookingStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("status required");
        }
        this.status = status;
    }

    // Displays booking details
    @Override
    public String toString() {
        return "Booking{" +
                "bookingId='" + bookingId + '\'' +
                ", userId=" + (user == null ? "null" : user.getID()) +
                ", eventId=" + (event == null ? "null" : event.getEventId()) +
                ", whenCreated=" + whenCreated +
                ", status=" + status +
                '}';
    }
}