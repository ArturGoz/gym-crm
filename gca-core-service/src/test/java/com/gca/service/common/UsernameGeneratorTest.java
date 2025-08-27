package com.gca.service.common;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.gca.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsernameGeneratorTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UsernameGenerator sut;

    @Test
    void generate_shouldReturnBaseNameIfNotExists() {
        when(userRepository.getAllUsernames()).thenReturn(emptyList());
        String result = sut.generate("Arnold", "Schwarzenegger");
        assertEquals("arnold.schwarzenegger", result);
    }

    @Test
    void generate_shouldReturnWithSuffixIfBaseTaken() {
        when(userRepository.getAllUsernames()).thenReturn(List.of("Arnold.Schwarzenegger"));
        String result = sut.generate("Arnold", "Schwarzenegger");
        assertEquals("arnold.schwarzenegger1", result);
    }

    @Test
    void generate_shouldIterateSuffixes() {
        when(userRepository.getAllUsernames()).thenReturn(Arrays.asList("Arnold.Schwarzenegger", "Arnold.Schwarzenegger1"));
        String result = sut.generate("Arnold", "Schwarzenegger");
        assertEquals("arnold.schwarzenegger2", result);
    }

    @Test
    void generate_shouldTreatCaseAsTheSame() {
        when(userRepository.getAllUsernames()).thenReturn(Collections.singletonList("arnold.schwarzenegger"));
        String result = sut.generate("Arnold", "Schwarzenegger");
        assertEquals("arnold.schwarzenegger1", result);
    }

    @ParameterizedTest
    @ValueSource(strings = {"John.doE", "john.doe", "John.doe", "JOHN.DOE", "JohN.doE"})
    void generate_shouldLogIfUsernameExist(String username) {
        Logger logger = (Logger) LoggerFactory.getLogger(UsernameGenerator.class);
        logger.setLevel(Level.DEBUG);

        when(userRepository.getAllUsernames()).thenReturn(List.of(username));

        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        logger.addAppender(listAppender);
        listAppender.start();

        sut.generate("john", "doe");

        assertEquals(2, listAppender.list.size());

        assertEquals(Level.WARN, listAppender.list.get(1).getLevel());
        assertTrue(listAppender.list.get(1).getFormattedMessage().equalsIgnoreCase(
                "User with username john.doe already exists, generated alternative username: john.doe1"));
    }
}