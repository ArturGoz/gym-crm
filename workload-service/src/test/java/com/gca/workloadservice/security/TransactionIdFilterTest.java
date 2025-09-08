package com.gca.workloadservice.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

import java.io.IOException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TransactionIdFilterTest {

    private static final String TRANSACTION_ID = "transactionId";
    private static final String TRANSACTION_ID_HEADER = "X-Transaction-Id";

    private TransactionIdFilter filter;
    private HttpServletRequest request;
    private ServletResponse response;
    private FilterChain chain;

    @BeforeEach
    void setUp() {
        filter = new TransactionIdFilter();
        request = mock(HttpServletRequest.class);
        response = mock(ServletResponse.class);
        chain = mock(FilterChain.class);
    }

    @AfterEach
    void clearMdc() {
        MDC.clear();
    }

    @Test
    void shouldUseProvidedTransactionId_whenHeaderExists() throws IOException, ServletException {
        String providedId = UUID.randomUUID().toString();
        when(request.getHeader(TRANSACTION_ID_HEADER)).thenReturn(providedId);

        filter.doFilter(request, response, chain);

        assertThat(MDC.get(TRANSACTION_ID)).isEqualTo(providedId);
        verify(chain).doFilter(request, response);
    }

    @Test
    void shouldGenerateTransactionId_whenHeaderMissing() throws IOException, ServletException {
        when(request.getHeader(TRANSACTION_ID_HEADER)).thenReturn(null);

        filter.doFilter(request, response, chain);

        String transactionId = MDC.get(TRANSACTION_ID);
        assertThat(transactionId)
                .isNotNull()
                .matches("^[0-9a-f\\-]{36}$");
        verify(chain).doFilter(request, response);
    }

    @Test
    void shouldGenerateTransactionId_whenHeaderEmpty() throws IOException, ServletException {
        when(request.getHeader(TRANSACTION_ID_HEADER)).thenReturn("");

        filter.doFilter(request, response, chain);

        String transactionId = MDC.get(TRANSACTION_ID);
        assertThat(transactionId)
                .isNotNull()
                .matches("^[0-9a-f\\-]{36}$");
        verify(chain).doFilter(request, response);
    }
}