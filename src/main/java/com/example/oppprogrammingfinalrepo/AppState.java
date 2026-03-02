package com.example.oppprogrammingfinalrepo;

import Booking_Management.Booking;
import Event_Management.Event;
import User_Management.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

// Stores all runtime data
// Allows the GUI and manager classes to share the same data structures preventing duplication
public class AppState {
    public final ArrayList<Event> events = new ArrayList<>();
    public final ArrayList<User> users = new ArrayList<>();

    // Hashmap used to find the booking much quicker rather than searching through the whole list
    public final Map<String, Booking> bookings = new HashMap<>();
}