package com.gca.repository.impl;

import com.gca.model.User;
import com.gca.repository.BaseIntegrationTest;
import com.gca.repository.UserRepository;
import com.github.database.rider.core.api.dataset.DataSet;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataSet(value = "dataset/user/user-data.xml", cleanBefore = true, cleanAfter = true, transactional = true)
public class UserDAOImplTest extends BaseIntegrationTest<UserRepository> {

    @Test
    void shouldReturnAllUsernames() {
        List<String> actual = repository.getAllUsernames();

        assertNotNull(actual);
        assertTrue(actual.contains("arnold.schwarzenegger"));
        assertTrue(actual.contains("ghost.user"));
        assertEquals(2, actual.size());
    }

    @Test
    void shouldUpdateUser() {
        User expected = buildUser();
        expected.setFirstName("UpdatedName");

        User actual = repository.save(expected);

        assertEquals(expected.getFirstName(), actual.getFirstName());
        assertEquals(expected.getUsername(), actual.getUsername());
        assertEquals(expected.getLastName(), actual.getLastName());
    }

    private User buildUser() {
        return User.builder()
                .id(1L)
                .firstName("Arnold")
                .lastName("Schwarzenegger")
                .username("arnold.schwarzenegger")
                .password("test123")
                .isActive(true)
                .build();
    }
}
