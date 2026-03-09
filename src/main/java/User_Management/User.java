package User_Management;

import enums.UserType;

import java.util.Objects;

public class User {

    private String name; //Lets know of attributes
    private String surname;
    private int day, month, year;
    private int id;
    private UserType userType;

    // Backward-compatible constructor (defaults to STUDENT)
    public User(String name, String surname, int month, int day, int year, int id) {
        this(name, surname, month, day, year, id, UserType.STUDENT);
    }

    // Full constructor
    public User(String name, String surname, int month, int day, int year, int id, UserType userType) {
        setName(name);
        setSurname(surname);
        setBirthdate(month, day, year);
        setID(id);
        setUserType(userType);
    }

    // Getters
    public String getName() { return name; }
    public String getSurname() { return surname; }
    public String getBirthdate() { return String.format("%02d/%02d/%04d", month, day, year); }
    public int getID() { return id; }
    public UserType getUserType() { return userType; }

    // Setters
    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) // Checks to see if name exists after spaces before and after are removed
            throw new IllegalArgumentException("Name cannot be empty.");
        this.name = name.trim();
    }

    public void setSurname(String surname) {
        if (surname == null || surname.trim().isEmpty())
            throw new IllegalArgumentException("Surname cannot be empty.");
        this.surname = surname.trim(); //Deletes unnessecary spaces
    }

    public void setBirthdate(int month, int day, int year) {
        if (year > 2026 || year < 1900)
            throw new IllegalArgumentException("Invalid year.");
        if (month < 1 || month > 12)
            throw new IllegalArgumentException("Invalid month.");
        if (day < 1 || day > 31)
            throw new IllegalArgumentException("Invalid day.");

        this.month = month;
        this.day = day;
        this.year = year;
    }

    public void setID(int id) {
        if (id < 0 || id > 9999999) //Should be 7 ID digits, should check if its always 7 digits
            throw new IllegalArgumentException("Invalid ID.");
        this.id = id;
    }

    public void setUserType(UserType userType) {
        if (userType == null)
            throw new IllegalArgumentException("UserType cannot be null.");
        this.userType = userType;
    }

    // Displays text
    @Override
    public String toString() {
        return String.format("%-10d %-15s %-15s %-12s %-10s", //Formatting
                id, name, surname, getBirthdate(), userType);
    }

    // Checks if 2 users are the same user
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User user)) return false; //If user doesn't already exist
        return id == user.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    } //Connects ID to values in HAshmap, if user leads to pre-existing space, notices duplicate
}