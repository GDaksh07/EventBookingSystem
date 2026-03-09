package Event_Management;

import enums.EventType;

import java.time.LocalDateTime;

public class Seminar extends Event {
    private static int nextId = 1;
    private String speakerName;

    public Seminar(String title, LocalDateTime dateTime, String location, int capacity, String speakerName) {
        super(generateId(), title, dateTime, location, capacity);
        setEventType(EventType.SEMINAR);
        setSpeakerName(speakerName);
    }

    // Generates a unique ID for the event
    private static String generateId() {
        return "SEM" + (nextId++);
    }

    // Getter
    public String getSpeakerName() {
        return speakerName;
    }

    // Setter
    public void setSpeakerName(String speakerName) {
        if (speakerName == null || speakerName.trim().isEmpty())
            throw new IllegalArgumentException("speakerName cannot be empty.");
        this.speakerName = speakerName.trim();
    }

    @Override
    public String toString() {
        return super.toString() + "\nSpeaker: " + speakerName.trim() + "\nEvent Type: " + this.getEventType();
    }
}
