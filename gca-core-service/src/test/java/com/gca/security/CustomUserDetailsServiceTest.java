package com.gca.security;

import com.gca.model.User;
import com.gca.repository.UserRepository;
import com.gca.utils.GymTestProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService userDetailsService;

    @Test
    void loadUserByUsername_whenUserExists_returnsUserDetails() {
        User expected = GymTestProvider.constructUser();

        when(userRepository.findByUsername(expected.getUsername()))
                .thenReturn(Optional.of(expected));

        UserDetails actual = userDetailsService.loadUserByUsername(expected.getUsername());

        assertEquals(expected.getUsername(), actual.getUsername());
        assertEquals(expected.getPassword(), actual.getPassword());
        assertTrue(actual.isEnabled());
        assertTrue(actual.isAccountNonExpired());
        assertTrue(actual.isAccountNonLocked());
        assertTrue(actual.isCredentialsNonExpired());
        assertTrue(actual.getAuthorities().isEmpty());
    }

    @Test
    void loadUserByUsername_whenUserDoesNotExist_throwsException() {
        when(userRepository.findByUsername("nonExistingUser"))
                .thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () ->
                userDetailsService.loadUserByUsername("nonExistingUser"));
    }
}