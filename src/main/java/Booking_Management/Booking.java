package Booking_Management;

import Event_Management.Event;
import User_Management.User;
import enums.BookingStatus;

import java.time.LocalDateTime;

public class Booking {

    // Unique ID for each booking
    private final String bookingId;

    // The user who made the booking
    private final User user;

    // The event being booked
    private final Event event;

    // The time the booking was created
    private final LocalDateTime whenCreated;

    // Current status (CONFIRMED, WAITLISTED, CANCELLED)
    private BookingStatus status;

    // Default constructor used when creating bookings in the app
    // This automatically sets the time to NOW
    public Booking(String bookingId, User user, Event event, BookingStatus status) {

        // Calls the full constructor and passes current time
        this(bookingId, user, event, LocalDateTime.now(), status);
    }

    // Second constructor used when loading bookings from CSV (Part 3.1)
    // This allows us to restore the ORIGINAL created time from file
    public Booking(String bookingId, User user, Event event, LocalDateTime whenCreated, BookingStatus status) {

        // Basic validation checks
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

        // Assign values to object
        this.bookingId = bookingId;
        this.user = user;
        this.event = event;
        this.whenCreated = whenCreated; // <-- IMPORTANT FIX for 3.1
        this.status = status;
    }

    // Getters
    public String getBookingId() {
        return bookingId;
    }

    public User getUser() {
        return user;
    }

    public Event getEvent() {
        return event;
    }

    public LocalDateTime getWhenCreated() {
        return whenCreated;
    }

    public BookingStatus getStatus() {
        return status;
    }

    // Setter for updating booking status
    public void setStatus(BookingStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("status required");
        }
        this.status = status;
    }

    // Displays booking info nicely
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