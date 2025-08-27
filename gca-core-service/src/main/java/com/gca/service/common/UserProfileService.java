package com.gca.service.common;

import com.gca.service.helper.RandomPasswordGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserProfileService {
    private final UsernameGenerator usernameGenerator;
    private final RandomPasswordGenerator randomPasswordGenerator;
    private final PasswordEncoder bCryptPasswordEncoder;

    public String generateUsername(String firstName, String lastName) {
        return usernameGenerator.generate(firstName, lastName);
    }

    public String encryptPassword(String password) {
        return bCryptPasswordEncoder.encode(password);
    }

    public String generatePassword() {
        return randomPasswordGenerator.generatePassword();
    }

    public boolean verifyPassword(String rawPassword, String encodedPassword) {
        return bCryptPasswordEncoder.matches(rawPassword, encodedPassword);
    }
}
