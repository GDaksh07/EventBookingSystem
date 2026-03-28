import Booking_Management.Booking;
import Event_Management.*;
import User_Management.User;
import Managers.*;
import enums.*;
import Waitlist_Management.WaitlistManager;

import java.time.LocalDateTime;

public class Testing {
    public static void main(String[] args) {
        // Initialize Managers and State as per Section 3.1 & 3.2 [cite: 16, 17]
        UserManager userManager = new UserManager();
        EventManager eventManager = new EventManager();
        BookingManager bookingManager = new BookingManager();
        WaitlistManager waitlistManager = new WaitlistManager();

        System.out.println("=== Scenario 3.2: Navigating the Campus Event Booking System ===\n");

        // 1. Setup: Create a small capacity event to test waitlist logic [cite: 20]
        Workshop logicWorkshop = new Workshop("W101", "Java Logic Workshop",
                LocalDateTime.now().plusDays(5), "Room 201", 2, "Backend Development");

        // 2. Setup: Create Users with different roles [cite: 17, 18]
        User student1 = new User("Alice", "Smith", 5, 15, 2002, 1001, "alice@uoguelph.ca", UserType.STUDENT);
        User student2 = new User("Bob", "Jones", 8, 22, 2001, 1002, "bob@uoguelph.ca", UserType.STUDENT);
        User staff1 = new User("Dr.", "Miller", 3, 10, 1985, 5001, "miller@uoguelph.ca", UserType.STAFF);

        System.out.println("--- Phase 1: Booking to Capacity ---");
        // Alice books first (Confirmed)
        String res1 = bookingManager.bookEvent("B001", student1, logicWorkshop);
        System.out.println("Alice Booking: " + res1); [cite: 7]

        // Bob books second (Confirmed - fills the 2nd spot)
        String res2 = bookingManager.bookEvent("B002", student2, logicWorkshop);
        System.out.println("Bob Booking: " + res2); [cite: 7]

        System.out.println("\n--- Phase 2: Waitlist Triggering ---");
        // Dr. Miller books third (Should be Waitlisted as capacity is 2) [cite: 7, 20]
        String res3 = bookingManager.bookEvent("B003", staff1, logicWorkshop);
        System.out.println("Dr. Miller Booking: " + res3);

        // Verify Statuses
        System.out.println("Workshop Confirmed Count: " + logicWorkshop.getConfirmedBookings().size()); [cite: 3]
        System.out.println("Workshop Waitlist Queue Size: " + logicWorkshop.getWaitlist().size()); [cite: 3]

        System.out.println("\n--- Phase 3: Cancellation and Auto-Promotion ---");
        // Alice cancels her booking [cite: 7, 20]
        System.out.println("Alice is cancelling her confirmed spot...");
        String cancelRes = bookingManager.cancelBooking("B001", logicWorkshop);
        System.out.println("Result: " + cancelRes);

        // Check if Dr. Miller was promoted from the waitlist automatically [cite: 7, 15]
        System.out.println("\n--- Final Validation ---");
        System.out.println("Is Dr. Miller now confirmed? Status check needed...");

        for (Booking b : logicWorkshop.getConfirmedBookings()) {
            if (b.getUser().equals(staff1)) {
                System.out.println("SUCCESS: Dr. Miller has been promoted to CONFIRMED."); [cite: 7]
            }
        }

        // 4. Duplicate Prevention Check [cite: 7, 20]
        System.out.println("\n--- Phase 4: Duplicate Prevention ---");
        String dupRes = bookingManager.bookEvent("B004", student2, logicWorkshop);
        System.out.println("Bob tries to book again: " + dupRes);
    }
}