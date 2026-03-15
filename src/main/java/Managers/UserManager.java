package Managers;

// Imports the User class
import User_Management.User;
import enums.UserType;
import java.util.ArrayList;
import java.util.Scanner;

public class UserManager {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in); //To input user data
        ArrayList<User> users = new ArrayList<>();

        int choice;
        do { // User menu for options
            System.out.println("\n=== USER MANAGEMENT MENU ===");
            System.out.println("1. Add User");
            System.out.println("2. View User Details");
            System.out.println("3. List All Users");
            System.out.println("4. Exit");
            System.out.print("Enter choice: ");

            choice = scanner.nextInt();
            scanner.nextLine(); // clear buffer

            switch (choice) {
                // Creates a user
                case 1 -> {
                    System.out.print("Enter First Name: ");
                    String name = scanner.nextLine();

                    System.out.print("Enter Surname: ");
                    String surname = scanner.nextLine();

                    System.out.print("Enter Birth Month (1-12): ");
                    int month = scanner.nextInt();
                    System.out.print("Enter Birth Day (1-31): ");
                    int day = scanner.nextInt();
                    System.out.print("Enter Birth Year (1900-2026): ");
                    int year = scanner.nextInt();
                    scanner.nextLine();

                    System.out.print("Enter ID (0-9999999): ");
                    int id = scanner.nextInt();
                    scanner.nextLine();

                    System.out.println("Select User Type:");
                    System.out.println("1. Student");
                    System.out.println("2. Staff");
                    System.out.println("3. Guest");
                    System.out.print("Enter choice: ");
                    int typeChoice = scanner.nextInt();
                    scanner.nextLine();

                    System.out.print("Enter Email: ");
                    String email = scanner.nextLine();

                    UserType userType;

                    switch (typeChoice) {
                        case 1 -> userType = UserType.STUDENT;
                        case 2 -> userType = UserType.STAFF;
                        case 3 -> userType = UserType.GUEST;
                        default -> {
                            System.out.println("Invalid user type. Defaulting to Guest.");
                            userType = UserType.GUEST;
                        }
                    }
                    // If anything failed it will catch it and gives a error message
                    try {
                        users.add(new User(name, surname, month, day, year, id, email, userType));
                        System.out.println("User added successfully!");
                    } catch (IllegalArgumentException e) {
                        System.out.println(e.getMessage());
                    }
                }

                // Find a specific user
                case 2 -> {
                    System.out.print("Enter ID to search: ");
                    int searchId = scanner.nextInt();
                    scanner.nextLine();

                    boolean found = false;
                    for (User u : users) {
                        if (u.getID() == searchId) {
                            System.out.println(u.toString());
                            found = true;
                            break;
                        }
                    }
                    if (!found) System.out.println("User not found.");
                }

                // Lists the users
                case 3 -> {
                    if (users.isEmpty()) {
                        System.out.println("No users available.");
                    } else {
                        System.out.printf("%-10s %-15s %-15s %-20s %-12s %-10s%n",
                                "ID", "Name", "Surname", "Email", "Birthdate", "Type");
                        for (User u : users) System.out.println(u);
                    }
                }

                // Exits program
                case 4 -> System.out.println("Exiting program...");
                default -> System.out.println("Invalid choice.");
            }
        } while (choice != 4);

        scanner.close();
    }
}