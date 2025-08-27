package com.gca.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Component
@Slf4j
public class RequestResponseLoggingFilter extends OncePerRequestFilter {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final Set<String> SENSITIVE_FIELDS = Set.of("password", "oldPassword", "newPassword");
    private static final String EMPTY_BODY = "[empty]";
    private static final String UNSUPPORTED_ENCODING = "[unsupported encoding]";

    private final List<String> skipUrls = Arrays.asList(
            "/favicon.ico",
            "/static/",
            "/css/",
            "/js/",
            "/images/"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        if (shouldSkipLogging(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        long startTime = System.currentTimeMillis();

        CachedBodyHttpServletRequest wrappedRequest = new CachedBodyHttpServletRequest(request);
        CachedBodyHttpServletResponse wrappedResponse = new CachedBodyHttpServletResponse(response);

        try {
            logRequest(wrappedRequest);
            filterChain.doFilter(wrappedRequest, wrappedResponse);

            byte[] responseBody = wrappedResponse.getBodyBytes();
            response.getOutputStream().write(responseBody);

            logResponse(wrappedRequest, wrappedResponse, startTime);
        } catch (Exception e) {
            log.error("Error in RequestResponseLoggingFilter", e);
            logErrorResponse(wrappedRequest, response, startTime, e);

            throw e;
        }
    }

    private void logRequest(CachedBodyHttpServletRequest request) {
        String method = request.getMethod();
        String uri = request.getRequestURI();
        String body = EMPTY_BODY;

        if (request.getContentLength() > 0) {
            String rawBody = request.getBody();
            body = readBody(rawBody);
        }

        log.info("Incoming Request: [{}] {} Body: {}", method, uri, body);
    }

    private void logResponse(CachedBodyHttpServletRequest request,
                             CachedBodyHttpServletResponse response,
                             long startTime) {
        long duration = System.currentTimeMillis() - startTime;
        int status = response.getStatus();
        String method = request.getMethod();
        String uri = request.getRequestURI();

        String body = getResponseBody(response);
        body = readBody(body);

        log.info("Response for [{} {}] returned status {} in {}ms. Body: {}",
                method, uri, status, duration, body);
    }

    private void logErrorResponse(CachedBodyHttpServletRequest request,
                                  HttpServletResponse response,
                                  long startTime,
                                  Exception exception) {
        long duration = System.currentTimeMillis() - startTime;
        String method = request.getMethod();
        String uri = request.getRequestURI();
        int status = response.getStatus();

        log.error("Response for [{} {}] failed with status {} in {}ms. Error: {}",
                method, uri, status, duration, exception.getMessage());
    }

    private String readBody(String body) {
        if (body == null || body.isEmpty() || EMPTY_BODY.equals(body) || UNSUPPORTED_ENCODING.equals(body)) {
            return body;
        }

        try {
            ObjectNode jsonNode = (ObjectNode) objectMapper.readTree(body);
            hideSensitiveData(jsonNode);

            return jsonNode.toString();
        } catch (Exception e) {
            log.warn("Failed to parse JSON body: {}", e.getMessage());

            return body;
        }
    }

    private void hideSensitiveData(ObjectNode node) {
        SENSITIVE_FIELDS.stream()
                .filter(node::has)
                .forEach(field -> node.put(field, "***"));
    }

    private String getResponseBody(CachedBodyHttpServletResponse response) {
        byte[] buf = response.getBodyBytes();

        if (buf.length == 0) {
            return EMPTY_BODY;
        }

        try {
            String encoding = response.getCharacterEncoding();
            if (encoding == null) {
                encoding = "UTF-8";
            }
            return new String(buf, encoding);
        } catch (Exception e) {
            return UNSUPPORTED_ENCODING;
        }
    }

    private boolean shouldSkipLogging(HttpServletRequest request) {
        String uri = request.getRequestURI();

        return skipUrls.stream().anyMatch(uri::startsWith);
    }
}