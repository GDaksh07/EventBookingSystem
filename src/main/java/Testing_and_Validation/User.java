import java.util.*;

enum UserType {
    STUDENT, STAFF, GUEST
}

class User {
    private String userId;
    private String name;
    private String email;
    private UserType type;
    private List<Booking> bookings;

    public User(String userId, String name, String email, UserType type) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.type = type;
        this.bookings = new ArrayList<>();
    }

    public String getUserId() { return userId; }
    public UserType getType() { return type; }
    public List<Booking> getBookings() { return bookings; }

    public void addBooking(Booking booking) {
        bookings.add(booking);
    }

    public void removeBooking(Booking booking) {
        bookings.remove(booking);
    }
}