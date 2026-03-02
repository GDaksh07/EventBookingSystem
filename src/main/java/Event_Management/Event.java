package Event_Management;

import Booking_Management.Booking;
import enums.EventStatus;
import enums.EventType;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Event {
    private final String eventId;
    private String title;
    private LocalDateTime dateTime;
    private String location;
    private int capacity;

    private EventStatus status;
    private EventType eventType;

    // Booking storage for this event
    private final List<Booking> confirmedBookings = new ArrayList<>();
    private final Queue<Booking> waitlist = new LinkedList<>();

    // Display formatting
    private static final DateTimeFormatter DISPLAY_FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    // Constructor used by subclasses (Concert/Seminar/Workshop)
    public Event(String eventID, String title, LocalDateTime dateTime, String location, int capacity) {
        if (eventID == null || eventID.isBlank()) throw new IllegalArgumentException("Event ID cannot be empty.");
        this.eventId = eventID.trim();

        setTitle(title);
        setDateTime(dateTime);
        setLocation(location);
        setCapacity(capacity);

        this.status = EventStatus.ACTIVE;
        this.eventType = null; // subclasses typically set this via setEventType(...)
    }

    // Getters
    public String getEventId() { return eventId; }
    public String getTitle() { return title; }
    public LocalDateTime getDateTime() { return dateTime; }
    public String getLocation() { return location; }
    public int getCapacity() { return capacity; }
    public EventStatus getStatus() { return status; }
    public EventType getEventType() { return eventType; }

    // Setters
    public void setTitle(String title) {
        if (title == null || title.trim().isEmpty())
            throw new IllegalArgumentException("Title cannot be empty.");
        this.title = title.trim();
    }

    public void setDateTime(LocalDateTime dateTime) {
        if (dateTime == null)
            throw new IllegalArgumentException("Date/time cannot be null.");
        this.dateTime = dateTime;
    }

    public void setLocation(String location) {
        if (location == null || location.trim().isEmpty())
            throw new IllegalArgumentException("Location cannot be empty.");
        this.location = location.trim();
    }

    public void setCapacity(int capacity) {
        if (capacity <= 0)
            throw new IllegalArgumentException("Capacity must be greater than 0.");
        this.capacity = capacity;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType; // can be null if not set yet
    }

    // Cancels an event if it hasn't been cancelled yet
    public void cancelEvent() {
        if (status == EventStatus.CANCELLED)
            throw new IllegalStateException("Event already cancelled.");
        status = EventStatus.CANCELLED;
    }

    // Checks if event has seats available
    public boolean hasCapacity(){
        return confirmedBookings.size() < capacity;
    }

    // Returns the list of confirmed bookings for the event
    public List<Booking> getConfirmedBookings(){
        return confirmedBookings;
    }

    //Returns the waitlist queue for this event
    public Queue<Booking> getWaitlist() {
        return waitlist;
    }

    // Adds a booking to the confirmed booking list
    public void addConfirmedBooking(Booking booking) {
        confirmedBookings.add(booking);
    }

    // Adds a booking to the event waitlist
    public void addToWaitlist(Booking booking) {
        waitlist.add(booking);
    }

    // removes a booking from a confirmed booking list
    public void removeConfirmedBooking(Booking booking) {
        confirmedBookings.remove(booking);
    }

    // removes a booking from the waitlist
    public void removeFromWaitlist(Booking booking) {
        waitlist.remove(booking);
    }

    public Booking pollWaitlist() {
        if (waitlist == null) return null;
        return waitlist.poll(); // returns null if empty
    }

    // Displays events
    @Override
    public String toString() {
        return "Event ID: " + eventId +
                "\nTitle: " + title +
                "\nDate Time: " + dateTime.format(DISPLAY_FMT) +
                "\nLocation: " + location +
                "\nCapacity: " + capacity +
                "\nStatus: " + status +
                // Checks to see if there is an event type otherwise it doesn't print anything
                (eventType == null ? "" : "\nEvent Type: " + eventType);
    }
}