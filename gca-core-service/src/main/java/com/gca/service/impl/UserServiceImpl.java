package com.gca.service.impl;

import com.gca.actuator.prometheus.UserRegistrationMetrics;
import com.gca.repository.UserRepository;
import com.gca.dto.PasswordChangeDTO;
import com.gca.dto.user.UserCreateDTO;
import com.gca.exception.ServiceException;
import com.gca.mapper.UserMapper;
import com.gca.model.User;
import com.gca.service.UserService;
import com.gca.service.common.UserProfileService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import static java.lang.String.format;

@Service
@Validated
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final UserProfileService userProfileService;
    private final UserMapper userMapper;
    private final UserRegistrationMetrics metrics;

    @Override
    public User createUser(@Valid UserCreateDTO request) {
        logger.debug("Creating user for {} {}", request.getFirstName(), request.getLastName());

        String username = userProfileService.generateUsername(request.getFirstName(), request.getLastName());
        String password = userProfileService.generatePassword();
        User user = userMapper.toEntity(request).toBuilder()
                .username(username)
                .password(password)
                .isActive(true)
                .build();

        metrics.incrementSuccessCount();
        logger.info("User: {}", user);
        return user;
    }

    @Override
    public boolean isUserCredentialsValid(String username, String rawPassword) {
        logger.debug("Checking if user credentials are valid");

        if (username == null || rawPassword == null) {
            return false;
        }

        return userRepository.findByUsername(username)
                .map(user -> userProfileService.verifyPassword(rawPassword, user.getPassword()))
                .orElse(false);
    }

    @Transactional
    @Override
    public void changeUserPassword(@Valid PasswordChangeDTO passwordChangeDTO) {
        String username = passwordChangeDTO.getUsername();
        logger.debug("Changing password for user with username: {}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(
                        format("User with username %s not found", username)
                ));
        if (!userProfileService.verifyPassword(passwordChangeDTO.getOldPassword(), user.getPassword())) {
            throw new ServiceException("Old password is wrong");
        }

        user.setPassword(userProfileService.encryptPassword(passwordChangeDTO.getNewPassword()));
        userRepository.save(user);

        logger.info("Changed password for user with username: {}", username);
    }

    @Transactional
    @Override
    public void toggleActiveStatus(String username, boolean isActive) {
        logger.debug("Toggling active status for user with username");

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("User with username %s not found", username)
                ));

        validateActiveStatus(user, isActive);
        user.setIsActive(isActive);

        User updatedUser = userRepository.save(user);
        logger.info("Toggled active status for user with username: {} to {}", user.getUsername(), updatedUser.getIsActive());
    }

    private void validateActiveStatus(User user, boolean isActive) {
        if (isActive == user.getIsActive()) {
            String status = isActive ? "activate" : "deactivate";
            throw new ServiceException(format("Could not %s user: user is already %sed", status, status));
        }
    }
}

