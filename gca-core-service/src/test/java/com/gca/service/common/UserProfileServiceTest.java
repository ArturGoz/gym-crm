package com.gca.service.common;

import com.gca.service.helper.RandomPasswordGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserProfileServiceTest {

    @Mock
    private UsernameGenerator usernameGenerator;
    @Mock
    private RandomPasswordGenerator randomPasswordGenerator;
    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @InjectMocks
    private UserProfileService service;

    @Test
    void generateUsername() {
        when(usernameGenerator.generate("Arnold", "Schwarzenegger")).thenReturn("Arnold.Schwarzenegger");

        String username = service.generateUsername("Arnold", "Schwarzenegger");

        assertEquals("Arnold.Schwarzenegger", username);
        verify(usernameGenerator).generate("Arnold", "Schwarzenegger");
    }

    @Test
    void generateRandomPassword() {
        when(randomPasswordGenerator.generatePassword()).thenReturn("aB12cD34eF");

        String password = service.generatePassword();

        assertEquals("aB12cD34eF", password);
        verify(randomPasswordGenerator).generatePassword();
    }

    @Test
    void encodePassword() {
        String password = "randomPassword";

        when(bCryptPasswordEncoder.encode(password)).thenReturn("***");

        String actual = service.encryptPassword(password);

        assertEquals("***", actual);
        verify(bCryptPasswordEncoder).encode(password);
    }
}