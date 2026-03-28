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

        if (event.getStatus() != EventStatus.ACTIVE) {
            return "Event is not bookable (not ACTIVE).";
        }

        // prevent duplicate booking
        for (Booking b : bookings.values()) {
            if (b.getUser().equals(user)
                    && b.getEvent().equals(event)
                    && b.getStatus() != BookingStatus.CANCELLED) {
                return "User already booked this event.";
            }
        }

        int limit = getBookingLimit(user);
        int confirmedBookings = countConfirmedBookings(user);

        Booking booking;

        // confirm only if user is under limit AND event has space
        if (event.hasCapacity() && confirmedBookings < limit) {

            booking = new Booking(
                    bookingId,
                    user,
                    event,
                    BookingStatus.CONFIRMED
            );

            event.addConfirmedBooking(booking);

        } else {

            booking = new Booking(
                    bookingId,
                    user,
                    event,
                    BookingStatus.WAITLISTED
            );

            event.addToWaitlist(booking);
        }

        // store booking
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

            while (promoted != null) {

                int confirmedBookings = countConfirmedBookings(promoted.getUser());
                int limit = getBookingLimit(promoted.getUser());

                if (confirmedBookings < limit) {
                    // Only promote if allowed
                    promoted.setStatus(BookingStatus.CONFIRMED);
                    event.addConfirmedBooking(promoted);
                    break;
                } else {
                    // Skip this user (they’re already full)
                    promoted = event.pollWaitlist();
                }
            }
        } else {
            event.removeFromWaitlist(booking);
        }

        return "Booking cancelled.";
    }

    /**
     * Counts bookings that are still active (CONFIRMED or WAITLISTED).
     */
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