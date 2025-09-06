package com.gca.workloadservice.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;

import static org.assertj.core.api.Assertions.assertThat;

class CustomUserDetailsServiceTest {

    private final CustomUserDetailsService service = new CustomUserDetailsService();

    @Test
    void buildUserFromToken_shouldReturnUserDetailsWithUsername() {
        String username = "testUser";

        UserDetails userDetails = service.buildUserFromToken(username);

        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo(username);
    }

    @Test
    void buildUserFromToken_shouldReturnUserWithEmptyPassword() {
        UserDetails userDetails = service.buildUserFromToken("anyUser");

        assertThat(userDetails.getPassword()).isEmpty();
    }

    @Test
    void buildUserFromToken_shouldReturnUserWithNoAuthorities() {
        UserDetails userDetails = service.buildUserFromToken("anyUser");

        assertThat(userDetails.getAuthorities()).isEmpty();
    }

    @Test
    void buildUserFromToken_shouldReturnEnabledAccount() {
        UserDetails userDetails = service.buildUserFromToken("anyUser");

        assertThat(userDetails.isAccountNonExpired()).isTrue();
        assertThat(userDetails.isAccountNonLocked()).isTrue();
        assertThat(userDetails.isCredentialsNonExpired()).isTrue();
        assertThat(userDetails.isEnabled()).isTrue();
    }
}
