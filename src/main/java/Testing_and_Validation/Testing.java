public class Testing {
    public static void main(String[] args) {

        // Assume managers are already implemented and loaded from CSV
        UserManager um = new UserManager();
        EventManager em = new EventManager();
        BookingManager bm = new BookingManager();

        // === 1. Launch & Load Validation ===
        System.out.println("=== Launch & Load Validation ===");
        System.out.println("Users loaded: " + um.getAllUsers().size());
        System.out.println("Events loaded: " + em.getAllEvents().size());

        // === 2. User Management Validation ===
        System.out.println("\n=== User Management Validation ===");
        um.getAllUsers().forEach(u ->
                System.out.println(u.getUserId() + " - " + u.getName() + " (" + u.getType() + ")")
        );

        // View details for one user
        User user1 = um.getUser("U001");
        System.out.println("Viewing user U001 details: " + user1.getName());

        // Add a new user
        User newUser = new User("U999", "Charlie Test", "charlie@test.com", UserType.GUEST);
        um.addUser(newUser);
        System.out.println("Added user: " + newUser.getUserId());

        // === 3. Event Management Validation ===
        System.out.println("\n=== Event Management Validation ===");
        em.getAllEvents().forEach(e ->
                System.out.println(e.getEventId() + " - " + e.getTitle() + " [" + e.getStatus() + "]")
        );

        // Create a new event (Workshop)
        Event newEvent = new Workshop("E999", "Test Workshop", 2, "Test Topic");
        em.addEvent(newEvent);
        System.out.println("Created event: " + newEvent.getEventId());

        // Cancel an event
        Event eventToCancel = em.getEvent("E001");
        if (eventToCancel != null) {
            eventToCancel.cancelEvent();
            System.out.println("Cancelled event: " + eventToCancel.getEventId());
        }

        // === 4. Booking & Waitlist Validation ===
        System.out.println("\n=== Booking & Waitlist Validation ===");

        Event bookingEvent = em.getEvent("E002"); // pick an active event
        if (bookingEvent != null) {
            // Book 3 users
            bm.createBooking("B1001", user1, bookingEvent);
            bm.createBooking("B1002", newUser, bookingEvent);

            // Cancel first booking to test auto-promotion
            bm.cancelBooking("B1001", bookingEvent);
        }

        // Try duplicate booking (should be blocked)
        bm.createBooking("B1003", newUser, bookingEvent);

        // === 5. Waitlist Management Validation ===
        System.out.println("\n=== Waitlist Management Validation ===");
        bookingEvent.waitlist.forEach(b ->
                System.out.println("Waitlisted: " + b.getUserId())
        );

        System.out.println("\n=== 3.2 Scenario Completed ===");
    }
}