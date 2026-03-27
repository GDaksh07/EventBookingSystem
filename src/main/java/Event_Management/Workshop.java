package Event_Management;

import enums.EventType;

import java.time.LocalDateTime;

public class Workshop extends Event {
    private static int nextId = 1;
    private String topic;

    // Normal constructor used when creating a new workshop in the app
    // This still auto-generates a new workshop ID
    public Workshop(String title, LocalDateTime dateTime, String location, int capacity, String topic) {
        this(generateId(), title, dateTime, location, capacity, topic);
    }

    // Second constructor used when loading workshops from file for Part 3.1
    // This lets us keep the exact event ID from the CSV file
    public Workshop(String eventId, String title, LocalDateTime dateTime, String location, int capacity, String topic) {
        super(eventId, title, dateTime, location, capacity);
        setEventType(EventType.WORKSHOP);
        setTopic(topic);
    }

    // Generates a unique ID for Workshop
    private static String generateId() {
        return "WS" + (nextId++);
    }

    // Getter
    public String getTopic() {
        return topic;
    }

    // Setter
    public void setTopic(String topic) {
        if (topic == null || topic.trim().isEmpty()) {
            throw new IllegalArgumentException("Topic cannot be empty.");
        }
        this.topic = topic.trim();
    }

    @Override
    public String toString() {
        return super.toString() + "\nTopic: " + topic.trim() + "\nEvent Type: " + this.getEventType();
    }
}