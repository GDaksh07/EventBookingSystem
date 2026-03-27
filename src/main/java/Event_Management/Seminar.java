package Event_Management;

import enums.EventType;

import java.time.LocalDateTime;

public class Seminar extends Event {
    private static int nextId = 1;
    private String speakerName;

    // Normal constructor (used in the app)
    // Auto-generates ID
    public Seminar(String title, LocalDateTime dateTime, String location, int capacity, String speakerName) {
        this(generateId(), title, dateTime, location, capacity, speakerName);
    }

    // New constructor for Part 3.1 (loading from CSV)
    // Uses the eventId from file instead of generating one
    public Seminar(String eventId, String title, LocalDateTime dateTime, String location, int capacity, String speakerName) {
        super(eventId, title, dateTime, location, capacity);
        setEventType(EventType.SEMINAR);
        setSpeakerName(speakerName);
    }

    // Generates unique ID for new seminars
    private static String generateId() {
        return "SEM" + (nextId++);
    }

    // Getter
    public String getSpeakerName() {
        return speakerName;
    }

    // Setter with validation
    public void setSpeakerName(String speakerName) {
        if (speakerName == null || speakerName.trim().isEmpty())
            throw new IllegalArgumentException("speakerName cannot be empty.");
        this.speakerName = speakerName.trim();
    }

    @Override
    public String toString() {
        return super.toString()
                + "\nSpeaker: " + speakerName
                + "\nEvent Type: " + this.getEventType();
    }
}