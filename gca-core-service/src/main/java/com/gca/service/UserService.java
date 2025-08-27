package com.gca.service;

import com.gca.dto.PasswordChangeDTO;
import com.gca.dto.user.UserCreateDTO;
import com.gca.model.User;
import jakarta.validation.Valid;

public interface UserService {
    User createUser(@Valid UserCreateDTO request);

    boolean isUserCredentialsValid(String username, String rawPassword);

    void changeUserPassword(@Valid PasswordChangeDTO passwordChangeDTO);

    void toggleActiveStatus(String username, boolean isActive);
}
