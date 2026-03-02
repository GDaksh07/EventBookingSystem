package Waitlist_Management;

import Event_Management.Event;
import User_Management.User;
import enums.EventStatus;

import java.time.LocalDateTime;
import java.util.*;

/**
 * WaitlistManager (Part 1.4)
 *
 * I built this to manage waitlists WITHOUT changing the existing Event/User classes.
 * Since our Event class currently doesn't store confirmed attendees or a waitlist,
 * this manager stores that info in HashMaps.
 *
 * Key idea:
 * - I use the Event object itself as the key in the maps.
 * - I store User objects directly (no need for getUserId()).
 *
 * This avoids method-name mismatches and keeps my code isolated from other modules.
 */
public class WaitlistManager {

    /**
     * For each event, stores confirmed users (these are the people with seats).
     * We keep order so roster display is consistent.
     */
    private final Map<Event, List<User>> confirmedByEvent = new HashMap<>();

    /**
     * For each event, stores waitlisted users in FIFO order.
     * Queue = first-come, first-served.
     */
    private final Map<Event, Queue<User>> waitlistByEvent = new HashMap<>();

    /**
     * For each event, tracks "active" users (either confirmed or waitlisted).
     * This prevents duplicate entries for the same event.
     */
    private final Map<Event, Set<User>> activeUsersByEvent = new HashMap<>();

    /**
     * Optional: track when a user joined the waitlist (useful for UI + testing).
     */
    private final Map<Event, Map<User, LocalDateTime>> waitlistTimestamps = new HashMap<>();

    /**
     * Helper: ensures all maps have an entry for the event before we use it.
     */
    private void init(Event event) {
        confirmedByEvent.putIfAbsent(event, new ArrayList<>());
        waitlistByEvent.putIfAbsent(event, new ArrayDeque<>());
        activeUsersByEvent.putIfAbsent(event, new HashSet<>());
        waitlistTimestamps.putIfAbsent(event, new HashMap<>());
    }

    /**
     * Helper: checks whether an event is active (bookable).
     * Your Event uses a String status like "Active"/"Cancelled".
     */
    private boolean isActive(Event event) {
        return event.getStatus() == EventStatus.ACTIVE;
    }

    /**
     * Add a user to confirmed roster if there is space.
     * If event is full, call addToWaitlist() instead.
     */
    public void addToConfirmed(Event event, User user) {
        init(event);

        if (!isActive(event)) {
            throw new IllegalStateException("Event is not Active. Cannot confirm booking.");
        }

        // Block duplicates (same person can't be confirmed/waitlisted twice)
        if (activeUsersByEvent.get(event).contains(user)) {
            throw new IllegalStateException("User is already confirmed or waitlisted for this event.");
        }

        // Capacity check
        if (confirmedByEvent.get(event).size() >= event.getCapacity()) {
            throw new IllegalStateException("Event is full. Add user to waitlist instead.");
        }

        confirmedByEvent.get(event).add(user);
        activeUsersByEvent.get(event).add(user);
    }

    /**
     * Add a user to the waitlist in FIFO order.
     */
    public void addToWaitlist(Event event, User user) {
        init(event);

        if (!isActive(event)) {
            throw new IllegalStateException("Event is not Active. Cannot join waitlist.");
        }

        if (activeUsersByEvent.get(event).contains(user)) {
            throw new IllegalStateException("User is already confirmed or waitlisted for this event.");
        }

        waitlistByEvent.get(event).add(user);
        waitlistTimestamps.get(event).put(user, LocalDateTime.now());
        activeUsersByEvent.get(event).add(user);
    }

    /**
     * Cancel a confirmed booking. If someone is waiting, promote them automatically.
     * Returns the promoted User (or null if no one to promote).
     */
    public User cancelConfirmedAndPromote(Event event, User user) {
        init(event);

        boolean removed = confirmedByEvent.get(event).remove(user);
        if (!removed) {
            throw new IllegalStateException("User is not confirmed for this event.");
        }

        activeUsersByEvent.get(event).remove(user);
        return promoteNextIfPossible(event);
    }

    /**
     * If there is space and waitlist is not empty, promote the first waitlisted user.
     * Returns promoted User or null.
     */
    public User promoteNextIfPossible(Event event) {
        init(event);

        if (!isActive(event)) return null;

        if (confirmedByEvent.get(event).size() >= event.getCapacity()) return null;

        User next = waitlistByEvent.get(event).poll(); // FIFO
        if (next == null) return null;

        waitlistTimestamps.get(event).remove(next);

        confirmedByEvent.get(event).add(next);
        // user stays in active set (still booked)
        return next;
    }

    /**
     * Remove a user from the waitlist (ex: if they cancel while waiting).
     */
    public void removeFromWaitlist(Event event, User user) {
        init(event);

        boolean removed = waitlistByEvent.get(event).remove(user);
        if (!removed) {
            throw new IllegalStateException("User is not on the waitlist for this event.");
        }

        waitlistTimestamps.get(event).remove(user);
        activeUsersByEvent.get(event).remove(user);
    }

    /**
     * When an event is cancelled:
     * Clear confirmed list, waitlist, and internal tracking for that event.
     */
    public void handleEventCancelled(Event event) {
        init(event);

        confirmedByEvent.get(event).clear();
        waitlistByEvent.get(event).clear();
        activeUsersByEvent.get(event).clear();
        waitlistTimestamps.get(event).clear();
    }

    /**
     * View confirmed roster (copy, so caller can't mutate internal list).
     */
    public List<User> viewConfirmed(Event event) {
        init(event);
        return new ArrayList<>(confirmedByEvent.get(event));
    }

    /**
     * View waitlist in FIFO order (copy).
     */
    public List<User> viewWaitlist(Event event) {
        init(event);
        return new ArrayList<>(waitlistByEvent.get(event));
    }
}