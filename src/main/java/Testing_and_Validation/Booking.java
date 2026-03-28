enum BookingStatus {
    CONFIRMED, WAITLISTED
}

class Booking {
    private User user;
    private Event event;
    private BookingStatus status;

    public Booking(User user, Event event, BookingStatus status) {
        this.user = user;
        this.event = event;
        this.status = status;
    }

    public User getUser() { return user; }
    public Event getEvent() { return event; }
    public BookingStatus getStatus() { return status; }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }
}