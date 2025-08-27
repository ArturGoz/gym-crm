package com.gca.service.impl;

import com.gca.actuator.prometheus.UserRegistrationMetrics;
import com.gca.exception.ServiceException;
import com.gca.utils.GymTestProvider;
import com.gca.repository.UserRepository;
import com.gca.dto.PasswordChangeDTO;
import com.gca.dto.user.UserCreateDTO;
import com.gca.mapper.UserMapper;
import com.gca.model.User;
import com.gca.service.common.UserProfileService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository dao;

    @Mock
    private UserProfileService profileService;

    @Mock
    private UserMapper mapper;

    @Mock
    private UserRegistrationMetrics metrics;

    @InjectMocks
    private UserServiceImpl service;

    @Test
    void createUser_success() {
        UserCreateDTO request = GymTestProvider.createUserCreateRequest();
        User saved = GymTestProvider.constructUser();

        when(profileService.generateUsername(request.getFirstName(), request.getLastName()))
                .thenReturn("arnold.schwarzenegger");
        when(profileService.generatePassword()).thenReturn("securePass123");
        when(mapper.toEntity(request)).thenReturn(saved);

        User actual = service.createUser(request);

        assertEquals(saved, actual);
        verify(profileService).generateUsername(request.getFirstName(), request.getLastName());
        verify(profileService).generatePassword();
        verify(metrics).incrementSuccessCount();
    }

    @Test
    void isUserCredentialsValid_validCredentials_returnsTrue() {
        User user = GymTestProvider.constructUser();

        when(dao.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(profileService.verifyPassword(any(String.class), any(String.class))).thenReturn(true);

        boolean result = service.isUserCredentialsValid(user.getUsername(), user.getPassword());

        assertTrue(result);
        verify(dao).findByUsername(user.getUsername());
    }

    @Test
    void isUserCredentialsValid_invalidPassword_returnsFalse() {
        User user = GymTestProvider.constructUser();

        when(dao.findByUsername(user.getUsername())).thenReturn(Optional.of(user));

        boolean result = service.isUserCredentialsValid(user.getUsername(), "wrongPass");

        assertFalse(result);
        verify(dao).findByUsername(user.getUsername());
    }

    @Test
    void changeUserPassword_success() {
        User actual = buildUser();
        PasswordChangeDTO request = buildUserPasswordChange();

        when(dao.findByUsername(actual.getUsername())).thenReturn(Optional.of(actual));
        when(profileService.verifyPassword(request.getOldPassword(), actual.getPassword())).thenReturn(true);
        when(profileService.encryptPassword(request.getNewPassword())).thenReturn("encryptedNewPass");

        service.changeUserPassword(request);

        assertEquals("encryptedNewPass", actual.getPassword());
        verify(dao).findByUsername(request.getUsername());
        verify(profileService).encryptPassword(request.getNewPassword());
        verify(dao).save(actual);
    }

    @Test
    void changeUserPassword_userNotFound_throwsException() {
        String username = "nonexistent.user";
        PasswordChangeDTO request = new PasswordChangeDTO(username,
                "anyOldPass", "newPass");

        when(dao.findByUsername(username)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () ->
                service.changeUserPassword(request)
        );

        assertEquals("User with username nonexistent.user not found", ex.getMessage());
        verify(dao).findByUsername(username);
    }


    @ParameterizedTest
    @CsvSource({
            "true, false",
            "false, true"
    })
    void toggleActiveStatus_success(boolean currentStatus, boolean newStatus) {
        User user = GymTestProvider.constructUser().toBuilder().isActive(currentStatus).build();
        User updatedUser = user.toBuilder().isActive(newStatus).build();

        when(dao.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(dao.save(any(User.class))).thenReturn(updatedUser);

        service.toggleActiveStatus(user.getUsername(), newStatus);

        verify(dao).save(any(User.class));
    }

    @ParameterizedTest
    @CsvSource({
            "true, true",
            "false, false"
    })
    void toggleActiveStatus_alreadyInStatus_throwsException(boolean currentStatus, boolean newStatus) {
        User user = GymTestProvider.constructUser().toBuilder().isActive(currentStatus).build();
        String username = user.getUsername();
        String expectedAction = newStatus ? "activate" : "deactivate";

        when(dao.findByUsername(username)).thenReturn(Optional.of(user));

        ServiceException ex = assertThrows(ServiceException.class, () ->
                service.toggleActiveStatus(username, newStatus)
        );

        assertTrue(ex.getMessage().contains(format("Could not %s user", expectedAction)));
    }

    private User buildUser() {
        return User.builder()
                .username("ronnie.coleman")
                .password("encryptedOldPass")
                .build();
    }

    private PasswordChangeDTO buildUserPasswordChange() {
        return new PasswordChangeDTO("ronnie.coleman",
                "123qweQWE!@#", "321ewqEWQ#@!");
    }
}
