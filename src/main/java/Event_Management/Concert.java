package Event_Management;

import enums.EventType;

import java.time.LocalDateTime;

public class Concert extends Event {
    private static int nextId = 1;
    private int ageRestriction;

    // Constructor used when creating a new concert in the app
    // Automatically generates a new ID
    public Concert(String title, LocalDateTime dateTime, String location, int capacity, int ageRestriction) {
        this(generateId(), title, dateTime, location, capacity, ageRestriction);
    }

    // Constructor that allows passing in a specific event ID
    public Concert(String eventId, String title, LocalDateTime dateTime, String location, int capacity, int ageRestriction) {
        super(eventId, title, dateTime, location, capacity);
        setEventType(EventType.CONCERT);
        setAgeRestriction(ageRestriction);
    }

    // Generates a unique ID for each concert
    private static String generateId() {
        return "CON" + (nextId++);
    }

    // Returns age restriction
    public int getAgeRestriction() {
        return ageRestriction;
    }

    // Sets age restriction with basic validation
    public void setAgeRestriction(int ageRestriction) {
        if (ageRestriction < 0) {
            throw new IllegalArgumentException("Age restriction cannot be negative.");
        }
        this.ageRestriction = ageRestriction;
    }

    // Displays concert details
    @Override
    public String toString() {
        return super.toString()
                + "\nAge Restriction: " + this.ageRestriction
                + "\nEvent Type: " + this.getEventType();
    }
}