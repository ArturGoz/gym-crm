package com.gca.security;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.nio.charset.StandardCharsets;
import java.util.Iterator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class RequestResponseLoggingFilterTest {

    private RequestResponseLoggingFilter filter;
    private ListAppender<ILoggingEvent> appender;

    @BeforeEach
    public void setUp() {
        filter = new RequestResponseLoggingFilter();
        Logger logger = (Logger) LoggerFactory.getLogger(RequestResponseLoggingFilter.class);

        appender = new ListAppender<>();
        appender.start();

        logger.addAppender(appender);
        appender.list.clear();
    }

    @Test
    void shouldMaskOldAndNewPasswordsInRequestLog() throws Exception {
        String requestBody = """
                {
                    "username": "user1",
                    "oldPassword": "qwerty1234",
                    "newPassword": "qwerty9999"
                }
                """;
        MockHttpServletRequest request = createJsonRequest("PUT", "/api/auth/change-password", requestBody);
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        filter.doFilterInternal(request, response, chain);

        verify(chain).doFilter(any(CachedBodyHttpServletRequest.class), any(CachedBodyHttpServletResponse.class));
        assertThat(getRequestLogEvent().getFormattedMessage())
                .as("Request log message masking check")
                .contains("Incoming Request: [PUT] /api/auth/change-password Body:" +
                        " {\"username\":\"user1\",\"oldPassword\":\"***\",\"newPassword\":\"***\"}")
                .doesNotContain("qwerty1234")
                .doesNotContain("qwerty9999");
    }

    @Test
    void shouldMaskPasswordInLoginRequestLog() throws Exception {
        String requestBody = """
                {
                    "username": "user1",
                    "password": "superSecret123"
                }
                """;
        MockHttpServletRequest request = createJsonRequest("POST", "/api/auth/login", requestBody);
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        filter.doFilterInternal(request, response, chain);

        verify(chain).doFilter(any(CachedBodyHttpServletRequest.class), any(CachedBodyHttpServletResponse.class));
        assertThat(getRequestLogEvent().getFormattedMessage())
                .as("Request log message should mask password")
                .contains("\"password\":\"***\"")
                .doesNotContain("superSecret123");
    }

    @Test
    void shouldMaskPasswordInRegisterResponseLog() throws Exception {
        String responseBody = """
                {
                    "username": "ronnie.coleman",
                    "password": "RawPassword123"
                }
                """;
        MockHttpServletRequest request = createJsonRequest("POST", "/api/v1/trainees/register", null);
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = createResponseChain(200, responseBody);

        filter.doFilterInternal(request, response, chain);

        assertThat(getResponseLogEvent().getFormattedMessage())
                .as("Response log message should mask password")
                .contains("\"password\":\"***\"")
                .doesNotContain("RawPassword123");
    }

    @Test
    void shouldLogHttpMethodAndEndpointAndStatusCodeInResponseLog() throws Exception {
        String responseBody = """
                {
                    "someField": "someValue"
                }
                """;
        MockHttpServletRequest request = createJsonRequest("POST", "/api/v1/trainees/register", null);
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = createResponseChain(201, responseBody);

        filter.doFilterInternal(request, response, chain);

        assertThat(getResponseLogEvent().getFormattedMessage())
                .as("Response log message should contain HTTP method, endpoint, and status code")
                .contains("[POST /api/v1/trainees/register]")
                .contains("status 201")
                .contains("in ")
                .contains("ms.");
    }

    @Test
    void shouldLogHttpMethodAndEndpointAndStatusCodeForGetRequest() throws Exception {
        String responseBody = """
                {
                    "username": "ronnie.coleman"
                }
                """;
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/trainees/");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = createResponseChain(200, responseBody);

        filter.doFilterInternal(request, response, chain);

        assertThat(getResponseLogEvent().getFormattedMessage())
                .as("Response log message should contain HTTP method, endpoint, and status code")
                .contains("[GET /api/v1/trainees/]")
                .contains("status 200")
                .contains("in ")
                .contains("ms.");
    }

    @Test
    void shouldLogHttpMethodAndEndpointAndStatusCodeForPutRequest() throws Exception {
        String responseBody = """
                {
                    "result": "success"
                }
                """;
        MockHttpServletRequest request =
                new MockHttpServletRequest("PUT", "/api/v1/trainees/ronnie.coleman");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = createResponseChain(204, responseBody);

        filter.doFilterInternal(request, response, chain);

        assertThat(getResponseLogEvent().getFormattedMessage())
                .as("Response log message should contain HTTP method, endpoint, and status code")
                .contains("[PUT /api/v1/trainees/ronnie.coleman]")
                .contains("status 204")
                .contains("in ")
                .contains("ms.");
    }

    @Test
    void shouldLogHttpMethodAndEndpointInRequestLog() throws Exception {
        String requestBody = """
                {
                    "username": "testuser",
                    "password": "password123"
                }
                """;
        MockHttpServletRequest request = createJsonRequest("POST", "/api/auth/login", requestBody);
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        filter.doFilterInternal(request, response, chain);

        verify(chain).doFilter(any(CachedBodyHttpServletRequest.class), any(CachedBodyHttpServletResponse.class));
        assertThat(appender.list.iterator().next().getFormattedMessage())
                .as("Request log message should contain HTTP method and endpoint")
                .contains("Incoming Request: [POST] /api/auth/login");
    }

    @Test
    void shouldLogHttpMethodAndEndpointInRequestLogForPut() throws Exception {
        String requestBody = """
                {
                    "username": "testuser",
                    "oldPassword": "oldpass",
                    "newPassword": "newpass"
                }
                """;
        MockHttpServletRequest request =
                createJsonRequest("PUT", "/api/auth/change-password", requestBody);
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        filter.doFilterInternal(request, response, chain);

        verify(chain).doFilter(any(CachedBodyHttpServletRequest.class), any(CachedBodyHttpServletResponse.class));
        assertThat(getRequestLogEvent().getFormattedMessage())
                .as("Request log message should contain HTTP method and endpoint")
                .contains("Incoming Request: [PUT] /api/auth/change-password");
    }

    @Test
    void shouldSkipLoggingForFaviconRequest() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/favicon.ico");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        filter.doFilterInternal(request, response, chain);

        verify(chain).doFilter(request, response);
        assertThat(appender.list).isEmpty();
    }

    private MockHttpServletRequest createJsonRequest(String method, String uri, String body) {
        MockHttpServletRequest request = new MockHttpServletRequest(method, uri);

        if (body != null) {
            request.setContent(body.getBytes(StandardCharsets.UTF_8));
        }

        request.setContentType("application/json");
        return request;
    }

    private FilterChain createResponseChain(int status, String responseBody) {
        return (req, res) -> {
            HttpServletResponse httpRes = (HttpServletResponse) res;
            httpRes.setStatus(status);
            httpRes.setContentType("application/json");
            httpRes.setCharacterEncoding("UTF-8");

            if (responseBody != null) {
                httpRes.getWriter().write(responseBody);
                httpRes.getWriter().flush();
            }
        };
    }

    private ILoggingEvent getRequestLogEvent() {
        if (appender.list.isEmpty()) {
            throw new AssertionError("No log events found");
        }

        return appender.list.iterator().next();
    }

    private ILoggingEvent getResponseLogEvent() {
        Iterator<ILoggingEvent> iterator = appender.list.iterator();

        if (!iterator.hasNext()) {
            throw new AssertionError("No response log event found");
        }

        iterator.next();

        if (!iterator.hasNext()) {
            throw new AssertionError("No response log event found");
        }

        return iterator.next();
    }
}