package Event_Management;

import enums.EventType;

import java.time.LocalDateTime;

public class Concert extends Event {
    private static int nextId = 1;
    private int ageRestriction;

    // Normal constructor used when creating a new concert in the app
    // This still auto-generates a new concert ID
    public Concert(String title, LocalDateTime dateTime, String location, int capacity, int ageRestriction) {
        this(generateId(), title, dateTime, location, capacity, ageRestriction);
    }

    // Second constructor used when loading concerts from file for Part 3.1
    // This lets us keep the exact event ID from the CSV file
    public Concert(String eventId, String title, LocalDateTime dateTime, String location, int capacity, int ageRestriction) {
        super(eventId, title, dateTime, location, capacity);
        setEventType(EventType.CONCERT);
        setAgeRestriction(ageRestriction);
    }

    // Generates a unique ID for Concert
    private static String generateId() {
        return "CON" + (nextId++);
    }

    // Getter
    public int getAgeRestriction() {
        return ageRestriction;
    }

    // Setter
    public void setAgeRestriction(int ageRestriction) {
        if (ageRestriction < 0) {
            throw new IllegalArgumentException("Age restriction cannot be negative.");
        }
        this.ageRestriction = ageRestriction;
    }

    @Override
    public String toString() {
        return super.toString() + "\nAge Restriction: " + this.ageRestriction + "\nEvent Type: " + this.getEventType();
    }
}