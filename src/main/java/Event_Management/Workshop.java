package Event_Management;

import enums.EventType;

import java.time.LocalDateTime;

public class Workshop extends Event {
    private static int nextId = 1;
    private String topic;

    // Constructor used when creating a workshop normally
    // Automatically generates a new ID
    public Workshop(String title, LocalDateTime dateTime, String location, int capacity, String topic) {
        this(generateId(), title, dateTime, location, capacity, topic);
    }

    // Constructor that allows passing in a specific event ID
    public Workshop(String eventId, String title, LocalDateTime dateTime, String location, int capacity, String topic) {
        super(eventId, title, dateTime, location, capacity);
        setEventType(EventType.WORKSHOP);
        setTopic(topic);
    }

    // Generates a unique ID for each workshop
    private static String generateId() {
        return "WS" + (nextId++);
    }

    // Returns workshop topic
    public String getTopic() {
        return topic;
    }

    // Sets topic with basic validation
    public void setTopic(String topic) {
        if (topic == null || topic.trim().isEmpty()) {
            throw new IllegalArgumentException("Topic cannot be empty.");
        }
        this.topic = topic.trim();
    }

    // Displays workshop details
    @Override
    public String toString() {
        return super.toString()
                + "\nTopic: " + topic.trim()
                + "\nEvent Type: " + this.getEventType();
    }
}