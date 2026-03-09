package Waitlist_Management;

import Event_Management.Event;
import User_Management.User;

// This class is just used to return promotion info back to the UI.
public class PromotionResult {

    private final boolean promoted;   // tells us if someone was promoted
    private final Event event;        // which event it happened for
    private final User promotedUser;  // which user got promoted

    public PromotionResult(boolean promoted, Event event, User promotedUser) {
        this.promoted = promoted;
        this.event = event;
        this.promotedUser = promotedUser;
    }

    public boolean isPromoted() {
        return promoted;
    }

    public Event getEvent() {
        return event;
    }

    public User getPromotedUser() {
        return promotedUser;
    }

    // Used when no promotion happens (instead of returning null)
    public static PromotionResult none(Event event) {
        return new PromotionResult(false, event, null);
    }
}