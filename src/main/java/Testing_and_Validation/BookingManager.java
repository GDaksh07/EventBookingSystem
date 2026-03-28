import java.util.*;

class BookingManager {
    private Map<String, User> users = new HashMap<>();
    private Map<String, Event> events = new HashMap<>();

    // -------- USER MANAGEMENT --------
    public void addUser(User user) {
        users.put(user.getUserId(), user);
    }

    public User getUser(String userId) {
        return users.get(userId);
    }

    public Collection<User> listUsers() {
        return users.values();
    }

    // -------- EVENT MANAGEMENT --------
    public void addEvent(Event event) {
        events.put(event.getEventId(), event);
    }

    public Event getEvent(String eventId) {
        return events.get(eventId);
    }

    public Collection<Event> listEvents() {
        return events.values();
    }

    public List<Event> searchByTitle(String keyword) {
        List<Event> results = new ArrayList<>();
        for (Event e : events.values()) {
            if (e.getTitle().toLowerCase().contains(keyword.toLowerCase())) {
                results.add(e);
            }
        }
        return results;
    }

    // -------- BOOKING LOGIC --------
    public String createBooking(String userId, String eventId) {
        User user = users.get(userId);
        Event event = events.get(eventId);

        if (event.getStatus() == EventStatus.CANCELLED) {
            return "Event is cancelled.";
        }

        // Prevent duplicate booking
        for (Booking b : user.getBookings()) {
            if (b.getEvent().getEventId().equals(eventId)) {
                return "Duplicate booking not allowed.";
            }
        }

        // Check booking limits
        int limit = getLimit(user.getType());
        long confirmedCount = user.getBookings().stream()
                .filter(b -> b.getStatus() == BookingStatus.CONFIRMED)
                .count();

        if (confirmedCount >= limit) {
            return "Booking limit exceeded for " + user.getType();
        }

        Booking booking;

        if (!event.isFull()) {
            booking = new Booking(user, event, BookingStatus.CONFIRMED);
            event.getConfirmed().add(booking);
        } else {
            booking = new Booking(user, event, BookingStatus.WAITLISTED);
            event.getWaitlist().add(booking);
        }

        user.addBooking(booking);
        return "Booking successful: " + booking.getStatus();
    }

    public void cancelBooking(String userId, String eventId) {
        User user = users.get(userId);
        Event event = events.get(eventId);

        Booking target = null;

        for (Booking b : user.getBookings()) {
            if (b.getEvent().getEventId().equals(eventId)) {
                target = b;
                break;
            }
        }

        if (target == null) return;

        user.removeBooking(target);
        event.getConfirmed().remove(target);

        // Promote waitlisted
        if (!event.getWaitlist().isEmpty()) {
            Booking next = event.getWaitlist().poll();
            next.setStatus(BookingStatus.CONFIRMED);
            event.getConfirmed().add(next);
        }
    }

    private int getLimit(UserType type) {
        switch (type) {
            case STUDENT: return 3;
            case STAFF: return 5;
            case GUEST: return 2;
            default: return 0;
        }
    }
}