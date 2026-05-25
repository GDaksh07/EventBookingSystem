# **OPP Programming Final Project**

**Event & Booking Management System**

### **OverView**
This project is a Java-based Event Management and Booking System built using:
- Java 21
- JavaFX
- Maven

This system allows users to:
- Create and manage events
- Register users
- Book events
- Handle waitlists
- Enforce booking limits based on user type
- Promote waitlisted users automatically when spots open

This application demonstrates core OOP concepts including encapsulation, abstraction, inheritance, polymorphism, enums, and manager/service-layer architecture.

### **Key Features**
**Event Management**
- Creates Concerts, Workshops, and Seminars
- Sets limits to number of seats for the event
- Able to view if the event is cancelled
- Able to see if a user is confirmed

**User Management**
- Creates validated users
- Unique identification
- User Types
  - Student (max 3 bookings)
  - Staff (max 5 bookings)
  - Guest (max 1 booking)

**Booking Management**
- Unique booking IDs 
- Duplicate booking prevention 
- Capacity enforcement 
- Automatic waitlist system 
- Automatic waitlist promotion when a confirmed booking is cancelled

### **Application Flow**
### **Main Menu**
Choose:
- Event Management 
- User Management 
- Booking Management
- Waitlist Viewer

### **Create Users**
Enter:
- First name
- Last name
- Birthdate
- Unique ID

Currently, users default to STUDENT will be changed later to support choosing if they are a student, staff or guests

#### **Create Events**
Choose:
- Event Type (Concert / Seminar / Workshop)
- Title
- DateTime (yyyy-MM-dd HH:mm)
- Location
- Capacity

Events start as ACTIVE

#### **Create Bookings**
Select:
- User
- Event
- Unique Booking ID 

System automatically:
- CONFIRMS booking if space available
- WAITLISTS booking if event is full

#### **Cancel Booking**

If CONFIRMED:
- Removes booking
- Promotes first waitlisted booking automatically
If WAITLISTED:
- Removes from waitlist

#### **Cancel Event**
- Event status becomes CANCELLED
- No further bookings allowed

### **How to Run the Program**
- Have Java 21
- Maven
- JavaFX configured by Maven

##### **Steps**
- Clone the repository using the url https://github.com/agabri01/OOProgrammingFinal
- Ensure you have SDK21 as well and Maven is detected on the right side
- In the Maven panel, click plugins, javafx, javafx:run or click the Run button at the top when on the "HelloApplication.java" file

### **How to Run the JUnit Suite Testing Files**
- Clone the repository using the url https://github.com/agabri01/OOProgrammingFinal
- Ensure you have SDK21 as well and Maven is detected on the right side
- Click on the Test folder on the left and click on the specific file you want to test
- In the .java file, you either click the top to run all tests in the file or right click the test you want to check and click run

### **Validation & Error Handling**
- IllegalArgumentException for invalid input
- Status validation before booking
- Guard clauses for null checks
- Prevents booking on CANCELLED events
- Prevents exceeding booking limits

### **Why This Project Matters**
This project demonstrates:
- Practical OOP application
- Real-world rule enforcement
- State transition management
- Clean separation between UI and logic
- Scalable architecture design
- Proper enum usage
- Modular package structure

It reflects backend-style thinking applied to a desktop GUI environment.

### **Authors**
- Project Manager: Daksh Gulati
- Aiden Gabriel
- Matthew Wojtkowski
- Muaz Umer
