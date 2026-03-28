public class Main {
    public static void main(String[] args) {
        BookingManager manager = new BookingManager();

        User u1 = new User("U1", "Alice", "a@email.com", UserType.STUDENT);
        manager.addUser(u1);

        Event e1 = new Event("E1", "Java Workshop", "2026-04-01", "Room A", 1);
        manager.addEvent(e1);

        System.out.println(manager.createBooking("U1", "E1")); // Confirmed

        User u2 = new User("U2", "Bob", "b@email.com", UserType.STUDENT);
        manager.addUser(u2);

        System.out.println(manager.createBooking("U2", "E1")); // Waitlisted

        manager.cancelBooking("U1", "E1"); // Bob promoted
    }
}