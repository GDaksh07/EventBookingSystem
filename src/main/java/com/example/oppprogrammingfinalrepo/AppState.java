package com.example.oppprogrammingfinalrepo;

import Booking_Management.Booking;
import Event_Management.Event;
import User_Management.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AppState {
    public final ArrayList<Event> events = new ArrayList<>();
    public final ArrayList<User> users = new ArrayList<>();
    public final Map<String, Booking> bookings = new HashMap<>();
}