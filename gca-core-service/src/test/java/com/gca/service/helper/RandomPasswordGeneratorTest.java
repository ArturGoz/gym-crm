package com.gca.service.helper;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RandomPasswordGeneratorTest {
    RandomPasswordGenerator sut = new RandomPasswordGenerator();

    @Test
    void generatePassword_shouldReturn10CharAlphaNumeric() {
        String password = sut.generatePassword();

        assertNotNull(password, "Password should not be null");
        assertEquals(10, password.length(),
                "Password should be 10 characters long");
        assertTrue(password.matches("^[A-Za-z0-9]{10}$"),
                "Password should be alphanumeric (A-Za-z0-9)");
    }

    @Test
    void generatePassword_shouldReturnRandomPasswords() {
        String password1 = sut.generatePassword();
        String password2 = sut.generatePassword();

        assertNotEquals(password1, password2, "Passwords should be random");
    }
}