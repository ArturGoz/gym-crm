package com.gca.security;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class CachedBodyHttpServletResponseTest {

    @Mock
    private HttpServletResponse mockResponse;

    @InjectMocks
    private CachedBodyHttpServletResponse cachedResponse;

    @Test
    void shouldWriteToOutputStreamAndReturnBytes() throws IOException {
        ServletOutputStream outputStream = cachedResponse.getOutputStream();

        outputStream.write("Hello".getBytes(StandardCharsets.UTF_8));

        String body = new String(cachedResponse.getBodyBytes(), StandardCharsets.UTF_8);
        assertThat(body).isEqualTo("Hello");
    }

    @Test
    void shouldWriteToWriterAndReturnBytes() {
        PrintWriter writer = cachedResponse.getWriter();

        writer.write("Test");
        writer.flush();

        String body = new String(cachedResponse.getBodyBytes(), StandardCharsets.UTF_8);
        assertThat(body).isEqualTo("Test");
    }

    @Test
    void outputStreamIsReadyShouldAlwaysBeTrue() {
        assertThat(cachedResponse.getOutputStream().isReady()).isTrue();
    }

    @Test
    void setWriteListenerShouldThrowUnsupportedOperationException() {
        assertThatThrownBy(() ->
                cachedResponse.getOutputStream().setWriteListener(mock(WriteListener.class))
        ).isInstanceOf(UnsupportedOperationException.class);
    }
}
