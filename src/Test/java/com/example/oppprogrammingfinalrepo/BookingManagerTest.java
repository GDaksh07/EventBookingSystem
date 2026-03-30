package com.example.oppprogrammingfinalrepo;

import Event_Management.Event;
import Event_Management.Workshop;
import Managers.BookingManager;
import User_Management.User;
import enums.UserType;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class BookingManagerTest {
    // Test: booking should be waitlisted when event capacity is full
    @Test
    void testBookingUnderCapacity() {
        // Create event with capacity 2
        Event e = new Workshop("W1", "Test", LocalDateTime.now(), "Room", 2, "Topic");

        // Create user
        User u = new User("A","B",1,1,2000,1,"a@email.com", UserType.STUDENT);

        BookingManager bm = new BookingManager(); // Create booking manager

        String result = bm.bookEvent("B1", u, e); // Attempt booking

        assertTrue(result.contains("CONFIRMED")); // Check that booking is confirmed
    }

    // Test: when event is full, new booking should go to waitlist
    @Test
    void testBookingWhenFullWaitlist() {
        // Create event with capacity = 1 (will fill quickly)
        Event e = new Workshop("W1", "Test", LocalDateTime.now(), "Room", 1, "Topic");

        // Create two users
        User u1 = new User("A","B",1,1,2000,1,"a@email.com", UserType.STUDENT);
        User u2 = new User("C","D",1,1,2000,2,"c@email.com", UserType.STUDENT);

        BookingManager bm = new BookingManager(); // Create booking manager

        bm.bookEvent("B1", u1, e); // First booking fills the event (CONFIRMED)

        String result = bm.bookEvent("B2", u2, e); // Second booking should go to waitlist

        assertTrue(result.contains("WAITLISTED")); // Verify that booking is waitlisted
    }

    // Test: cancelling a confirmed booking should promote a waitlisted user
    @Test
    void testCancelPromotesWaitlist() {
        // Create event with capacity = 1
        Event e = new Workshop("W1", "Test", LocalDateTime.now(), "Room", 1, "Topic");

        // Create two users
        User u1 = new User("A","B",1,1,2000,1,"a@email.com", UserType.STUDENT);
        User u2 = new User("C","D",1,1,2000,2,"c@email.com", UserType.STUDENT);

        BookingManager bm = new BookingManager(); // Create booking manager

        bm.bookEvent("B1", u1, e); // First user gets confirmed booking

        bm.bookEvent("B2", u2, e); // Second user gets waitlisted

        bm.cancelBooking("B1"); // Cancel the confirmed booking (should trigger promotion)

        assertEquals(1, e.getConfirmedBookings().size()); // Check that one booking is now confirmed (u2 should be promoted)
    }

    // Test: user should not be able to book the same event twice
    @Test
    void testDuplicateBookingPrevention() {
        // Create event with enough capacity
        Event e = new Workshop("W1", "Test", LocalDateTime.now(), "Room", 2, "Topic");

        // Create one user
        User u = new User("A","B",1,1,2000,1,"a@email.com", UserType.STUDENT);

        BookingManager bm = new BookingManager(); // Create booking manager

        bm.bookEvent("B1", u, e); // First booking should succeed

        String result = bm.bookEvent("B2", u, e); // Second booking attempt for same event should fail

        assertTrue(result.contains("already booked")); // Verify duplicate booking is prevented
    }
}