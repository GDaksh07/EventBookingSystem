package Waitlist_Management;

import Event_Management.Event;
import User_Management.User;
import enums.EventStatus;

import java.time.LocalDateTime;
import java.util.*;

// This class manages confirmed bookings and waitlists for events.
// It does not modify the Event or User classes.
// Instead, it stores everything internally using maps.
public class WaitlistManager {

    // Stores confirmed users per event (these users have seats).
    private final Map<Event, List<User>> confirmedByEvent = new HashMap<>();

    // Stores waitlisted users per event in FIFO order (first come, first served).
    private final Map<Event, Queue<User>> waitlistByEvent = new HashMap<>();

    // Tracks all users involved in an event (confirmed or waitlisted).
    // This prevents duplicate bookings.
    private final Map<Event, Set<User>> activeUsersByEvent = new HashMap<>();

    // Optional: keeps track of when a user joined the waitlist.
    // Useful if you want to show timestamps later.
    private final Map<Event, Map<User, LocalDateTime>> waitlistTimestamps = new HashMap<>();

    // Ensures all internal collections exist before using them.
    private void init(Event event) {
        confirmedByEvent.putIfAbsent(event, new ArrayList<>());
        waitlistByEvent.putIfAbsent(event, new ArrayDeque<>());
        activeUsersByEvent.putIfAbsent(event, new HashSet<>());
        waitlistTimestamps.putIfAbsent(event, new HashMap<>());
    }

    // Checks whether the event is active and can accept bookings.
    private boolean isActive(Event event) {
        return event.getStatus() == EventStatus.ACTIVE;
    }

    // Adds a user directly to confirmed list if there is space.
    public void addToConfirmed(Event event, User user) {

        init(event);

        if (!isActive(event)) {
            throw new IllegalStateException("Event is not Active.");
        }

        // Prevent duplicate booking
        if (activeUsersByEvent.get(event).contains(user)) {
            throw new IllegalStateException("User already booked for this event.");
        }

        // Check event capacity
        if (confirmedByEvent.get(event).size() >= event.getCapacity()) {
            throw new IllegalStateException("Event is full.");
        }

        confirmedByEvent.get(event).add(user);
        activeUsersByEvent.get(event).add(user);
    }

    // Adds a user to the waitlist (FIFO).
    public void addToWaitlist(Event event, User user) {

        init(event);

        if (!isActive(event)) {
            throw new IllegalStateException("Event is not Active.");
        }

        if (activeUsersByEvent.get(event).contains(user)) {
            throw new IllegalStateException("User already booked for this event.");
        }

        waitlistByEvent.get(event).add(user);
        waitlistTimestamps.get(event).put(user, LocalDateTime.now());
        activeUsersByEvent.get(event).add(user);
    }

    // Cancels a confirmed booking and promotes next user if possible.
    // Returns the promoted user (or null if no one to promote).
    public User cancelConfirmedAndPromote(Event event, User user) {

        init(event);

        boolean removed = confirmedByEvent.get(event).remove(user);

        if (!removed) {
            throw new IllegalStateException("User is not confirmed for this event.");
        }

        activeUsersByEvent.get(event).remove(user);

        return promoteNextIfPossible(event);
    }

    // Promotes first waitlisted user if there is space.
    // Returns promoted user or null.
    public User promoteNextIfPossible(Event event) {

        init(event);

        if (!isActive(event)) return null;

        // If still full, do nothing
        if (confirmedByEvent.get(event).size() >= event.getCapacity()) return null;

        User next = waitlistByEvent.get(event).poll(); // FIFO
        if (next == null) return null;

        waitlistTimestamps.get(event).remove(next);

        confirmedByEvent.get(event).add(next);

        return next;
    }

    // Removes a user from waitlist (ex: if they cancel while waiting).
    public void removeFromWaitlist(Event event, User user) {

        init(event);

        boolean removed = waitlistByEvent.get(event).remove(user);

        if (!removed) {
            throw new IllegalStateException("User not on waitlist.");
        }

        waitlistTimestamps.get(event).remove(user);
        activeUsersByEvent.get(event).remove(user);
    }

    // Clears all data when an event is cancelled.
    public void handleEventCancelled(Event event) {

        init(event);

        confirmedByEvent.get(event).clear();
        waitlistByEvent.get(event).clear();
        activeUsersByEvent.get(event).clear();
        waitlistTimestamps.get(event).clear();
    }

    // Returns confirmed users (copy so internal list can't be modified externally).
    public List<User> viewConfirmed(Event event) {
        init(event);
        return new ArrayList<>(confirmedByEvent.get(event));
    }

    // Returns waitlisted users in FIFO order (copy).
    public List<User> viewWaitlist(Event event) {
        init(event);
        return new ArrayList<>(waitlistByEvent.get(event));
    }

    // Helper for 2.4 to return promotion result cleanly.
    public PromotionResult cancelConfirmedWithResult(Event event, User user) {

        User promoted = cancelConfirmedAndPromote(event, user);

        if (promoted == null) {
            return PromotionResult.none(event);
        }

        return new PromotionResult(true, event, promoted);
    }
}