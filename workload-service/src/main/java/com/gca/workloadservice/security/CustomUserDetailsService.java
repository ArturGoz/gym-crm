package com.gca.workloadservice.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService {

    /**
     * Builds a {@link UserDetails} object based on the provided username.
     * <p>
     * The password field is intentionally left blank since the authentication
     * process has already been performed via JWT. At this stage, the method
     * simply creates a Spring Security {@link org.springframework.security.core.userdetails.User}
     * instance with the given username and no authorities.
     *
     * @param username the username extracted from the JWT
     * @return a {@link UserDetails} instance representing the authenticated user
     */
    public UserDetails buildUserFromToken(String username) {
        return new org.springframework.security.core.userdetails.User(
                username,
                "",
                List.of()
        );
    }
}


