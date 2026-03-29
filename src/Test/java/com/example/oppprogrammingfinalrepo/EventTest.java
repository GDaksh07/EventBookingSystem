package com.example.oppprogrammingfinalrepo;

import Event_Management.Event;
import Event_Management.Workshop;
import Managers.BookingManager;
import User_Management.User;
import enums.EventStatus;
import enums.UserType;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class EventTest {
    // Test: event should change status to cancelled when cancelEvent() is called
    @Test
    void testEventCancellation() {
        // Create event
        Event e = new Workshop("W1", "Test", LocalDateTime.now(), "Room", 2, "Topic");

        e.cancelEvent(); // Cancel the event

        assertEquals(EventStatus.CANCELLED, e.getStatus()); // Verify status is cancelled
    }

    // Test: event should report no capacity when full
    @Test
    void testEventCapacityFull() {

        // Create event with capacity = 1
        Event e = new Workshop("W1", "Test", LocalDateTime.now(), "Room", 1, "Topic");

        // Create user
        User u = new User("A","B",1,1,2000,1,"a@email.com", UserType.STUDENT);

        BookingManager bm = new BookingManager(); // Create booking manager

        bm.bookEvent("B1", u, e); // Fill capacity

        assertFalse(e.hasCapacity()); // Verify event is full
    }
}
