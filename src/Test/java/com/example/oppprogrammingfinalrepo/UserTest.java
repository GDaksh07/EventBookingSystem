package com.example.oppprogrammingfinalrepo;

import User_Management.User;
import enums.UserType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserTest {
    // Test: user should be created successfully with valid inputs
    @Test
    void testValidUserCreation() {
        // Creates user
        User u = new User("A","B",1,1,2000,1,"a@email.com", UserType.STUDENT);

        // Verify user properties
        assertEquals("A", u.getName());
        assertEquals(1, u.getID());
    }

    // Test: creating a user with invalid ID should throw exception
    @Test
    void testInvalidUserID() {
        // Expect exception when ID is invalid
        assertThrows(IllegalArgumentException.class, () -> {
            new User("A","B",1,1,2000,-1,"a@email.com", UserType.STUDENT);
        });
    }
}
