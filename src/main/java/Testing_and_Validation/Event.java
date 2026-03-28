import java.util.*;

enum EventStatus {
    ACTIVE, CANCELLED
}

class Event {
    private String eventId;
    private String title;
    private String dateTime;
    private String location;
    private int capacity;
    private EventStatus status;

    private List<Booking> confirmed;
    private Queue<Booking> waitlist;

    public Event(String eventId, String title, String dateTime, String location, int capacity) {
        this.eventId = eventId;
        this.title = title;
        this.dateTime = dateTime;
        this.location = location;
        this.capacity = capacity;
        this.status = EventStatus.ACTIVE;
        this.confirmed = new ArrayList<>();
        this.waitlist = new LinkedList<>();
    }

    public String getEventId() { return eventId; }
    public String getTitle() { return title; }
    public EventStatus getStatus() { return status; }

    public List<Booking> getConfirmed() { return confirmed; }
    public Queue<Booking> getWaitlist() { return waitlist; }

    public boolean isFull() {
        return confirmed.size() >= capacity;
    }

    public void cancelEvent() {
        status = EventStatus.CANCELLED;
    }
}