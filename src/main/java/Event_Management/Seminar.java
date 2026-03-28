package Event_Management;

import enums.EventType;

import java.time.LocalDateTime;

public class Seminar extends Event {
    private static int nextId = 1;
    private String speakerName;

    // Constructor used when creating a seminar normally
    // Automatically generates a new ID
    public Seminar(String title, LocalDateTime dateTime, String location, int capacity, String speakerName) {
        this(generateId(), title, dateTime, location, capacity, speakerName);
    }

    // Constructor that allows passing in a specific event ID
    public Seminar(String eventId, String title, LocalDateTime dateTime, String location, int capacity, String speakerName) {
        super(eventId, title, dateTime, location, capacity);
        setEventType(EventType.SEMINAR);
        setSpeakerName(speakerName);
    }

    // Generates a unique ID for each seminar
    private static String generateId() {
        return "SEM" + (nextId++);
    }

    // Returns speaker name
    public String getSpeakerName() {
        return speakerName;
    }

    // Sets speaker name with basic validation
    public void setSpeakerName(String speakerName) {
        if (speakerName == null || speakerName.trim().isEmpty())
            throw new IllegalArgumentException("speakerName cannot be empty.");
        this.speakerName = speakerName.trim();
    }

    // Displays seminar details
    @Override
    public String toString() {
        return super.toString()
                + "\nSpeaker: " + speakerName
                + "\nEvent Type: " + this.getEventType();
    }
}