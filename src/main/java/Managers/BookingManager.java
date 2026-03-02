package Managers;

import Booking_Management.Booking;
import Event_Management.Event;
import User_Management.User;
import enums.BookingStatus;
import enums.EventStatus;
import enums.UserType;

import java.util.HashMap;
import java.util.Map;

public class BookingManager {

    // All bookings stored by bookingId
    private final Map<String, Booking> bookings = new HashMap<>();

    /**
     * Books a user into an event.
     * - If event has space -> CONFIRMED
     * - If event full -> WAITLISTED
     * - Enforces per-user booking limits by UserType
     */
    public String bookEvent(String bookingId, User user, Event event) {

        if (bookingId == null || bookingId.isBlank()) {
            return "Invalid bookingId.";
        }
        if (user == null || event == null) {
            return "Invalid user or event.";
        }

        // Event must be active
        if (event.getStatus() != EventStatus.ACTIVE) {
            return "Event is not bookable (not ACTIVE).";
        }

        // Prevent duplicate booking (same user + same event, not cancelled)
        for (Booking b : bookings.values()) {
            if (b.getUser().equals(user)
                    && b.getEvent().equals(event)
                    && b.getStatus() != BookingStatus.CANCELLED) {
                return "User already booked this event.";
            }
        }

        // Enforce booking limits based on user type
        int limit = getBookingLimit(user);
        int activeBookings = countActiveBookings(user); // counts CONFIRMED + WAITLISTED

        if (activeBookings >= limit) {
            return "Booking limit reached for " + user.getUserType() + " (limit " + limit + ").";
        }

        // Decide confirmed vs waitlisted
        Booking booking;
        if (event.hasCapacity()) {
            booking = new Booking(bookingId, user, event, BookingStatus.CONFIRMED);
            event.addConfirmedBooking(booking);
        } else {
            booking = new Booking(bookingId, user, event, BookingStatus.WAITLISTED);
            event.addToWaitlist(booking);
        }

        bookings.put(bookingId, booking);
        return "Booking successful: " + booking.getStatus();
    }

    /**
     * Cancels a booking:
     * - If confirmed, remove it and promote 1 from waitlist (if any)
     * - If waitlisted, just remove it from waitlist
     */
    public String cancelBooking(String bookingId) {

        if (bookingId == null || bookingId.isBlank()) {
            return "Invalid bookingId.";
        }

        Booking booking = bookings.get(bookingId);
        if (booking == null) {
            return "Booking not found.";
        }

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            return "Already cancelled.";
        }

        Event event = booking.getEvent();
        boolean wasConfirmed = (booking.getStatus() == BookingStatus.CONFIRMED);

        // Mark cancelled first
        booking.setStatus(BookingStatus.CANCELLED);

        if (wasConfirmed) {
            event.removeConfirmedBooking(booking);

            // Promote first waitlisted booking (if exists)
            Booking promoted = event.pollWaitlist();
            if (promoted != null) {
                promoted.setStatus(BookingStatus.CONFIRMED);
                event.addConfirmedBooking(promoted);
            }
        } else {
            event.removeFromWaitlist(booking);
        }

        return "Booking cancelled.";
    }

    /**
     * Counts bookings that are still active (CONFIRMED or WAITLISTED).
     */
    private int countActiveBookings(User user) {
        int count = 0;
        for (Booking b : bookings.values()) {
            if (b.getUser().equals(user)
                    && b.getStatus() != BookingStatus.CANCELLED) {
                count++;
            }
        }
        return count;
    }

    /**
     * Booking limit rules by user type.
     */
    private int getBookingLimit(User user) {
        UserType type = user.getUserType();
        return switch (type) {
            case STUDENT -> 3;
            case STAFF -> 5;
            case GUEST -> 1;
        };
    }

    /**
     * Prints roster lists for one event.
     */
    public void viewEventRoster(Event event) {
        System.out.println("Confirmed:");
        for (Booking b : event.getConfirmedBookings()) {
            System.out.println("- " + b.getUser().getName());
        }

        System.out.println("\nWaitlist:");
        for (Booking b : event.getWaitlist()) {
            System.out.println("- " + b.getUser().getName());
        }
    }
}