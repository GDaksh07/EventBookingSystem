package Event_Management;

import enums.EventType;

import java.time.LocalDateTime;

public class Workshop extends Event {
    private static int nextId = 1;
    private String topic;

    public Workshop(String title, LocalDateTime dateTime, String location, int capacity, String topic) {
        super(generateId(), title, dateTime, location, capacity);
        setEventType(EventType.WORKSHOP);
        setTopic(topic);
    }

    // Generates a unique ID for the event
    private static String generateId() {
        return "WS" + (nextId++);
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        if (topic == null || topic.trim().isEmpty())
            throw new IllegalArgumentException("Topic cannot be empty.");
        this.topic = topic.trim();
    }

    @Override
    public String toString() {
        return super.toString() + "\nTopic: " + topic.trim() + "\nEvent Type: " + this.getEventType();
    }
}