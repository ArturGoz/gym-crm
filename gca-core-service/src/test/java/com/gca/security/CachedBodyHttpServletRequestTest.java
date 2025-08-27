package com.gca.security;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.StreamUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CachedBodyHttpServletRequestTest {

    @Mock
    private HttpServletRequest mockRequest;

    private String bodyContent;
    private CachedBodyHttpServletRequest cachedRequest;

    @BeforeEach
    void setUp() throws IOException {
        bodyContent = "{\"username\":\"testUser\",\"password\":\"12345\"}";

        when(mockRequest.getInputStream()).thenReturn(createServletInputStream(bodyContent));

        cachedRequest = new CachedBodyHttpServletRequest(mockRequest);
    }

    @Test
    void shouldCacheBodyAndReturnSameContentFromGetBody() {
        assertThat(cachedRequest.getBody()).isEqualTo(bodyContent);
    }

    @Test
    void shouldReturnSameBodyFromGetReader() {
        String fromReader = cachedRequest.getReader().lines().reduce("", (a, b) -> a + b);
        assertThat(fromReader).isEqualTo(bodyContent);
    }

    @Test
    void shouldReturnSameBodyFromGetInputStream() throws IOException {
        String fromStream = StreamUtils.copyToString(cachedRequest.getInputStream(), StandardCharsets.UTF_8);
        assertThat(fromStream).isEqualTo(bodyContent);
    }

    @Test
    void cachedServletInputStreamShouldReportFinishedAfterReading() throws IOException {
        ServletInputStream inputStream = cachedRequest.getInputStream();
        inputStream.transferTo(OutputStream.nullOutputStream());
        assertThat(inputStream.isFinished()).isTrue();
    }

    @Test
    void cachedServletInputStreamIsReadyShouldAlwaysBeTrue() {
        assertThat(cachedRequest.getInputStream().isReady()).isTrue();
    }

    @Test
    void setReadListenerShouldThrowUnsupportedOperationException() {
        assertThatThrownBy(() ->
                cachedRequest.getInputStream().setReadListener(mock(ReadListener.class))
        ).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void isFinishedShouldBeFalseBeforeReadingAndTrueAfterReading() throws IOException {
        ServletInputStream inputStream = cachedRequest.getInputStream();

        assertThat(inputStream.isFinished()).isFalse();
        inputStream.transferTo(OutputStream.nullOutputStream());
        assertThat(inputStream.isFinished()).isTrue();
    }

    private ServletInputStream createServletInputStream(String content) {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
        return new ServletInputStream() {
            @Override
            public int read() {
                return inputStream.read();
            }

            @Override
            public boolean isFinished() {
                return inputStream.available() == 0;
            }

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setReadListener(ReadListener readListener) {
                throw new UnsupportedOperationException();
            }
        };
    }
}