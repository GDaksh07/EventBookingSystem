package Booking_Management;

import Event_Management.Event;
import User_Management.User;
import enums.BookingStatus;

import java.time.LocalDateTime;

public class Booking {

    private final String bookingId;
    private final User user;
    private final Event event;
    private final LocalDateTime whenCreated;
    private BookingStatus status;

    public Booking(String bookingId, User user, Event event, BookingStatus status) {
        if (bookingId == null || bookingId.isBlank()) {
            throw new IllegalArgumentException("bookingId required");
        }
        if (user == null) {
            throw new IllegalArgumentException("user required");
        }
        if (event == null) {
            throw new IllegalArgumentException("event required");
        }
        if (status == null) {
            throw new IllegalArgumentException("status required");
        }

        this.bookingId = bookingId;
        this.user = user;
        this.event = event;
        this.whenCreated = LocalDateTime.now();
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

    // Setter
    public void setStatus(BookingStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("status required");
        }
        this.status = status;
    }

    // Display for user
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