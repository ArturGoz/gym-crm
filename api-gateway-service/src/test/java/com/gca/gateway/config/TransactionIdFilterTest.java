package com.gca.gateway.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionIdFilterTest {

    private static final String UUID_PATTERN =
            "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[1-5][0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$";

    private static final String TRANSACTION_ID_HEADER = "X-Transaction-Id";
    private static final String TRANSACTION_ID = "transactionId";
    private static final int FILTER_ORDER = -100;

    @InjectMocks
    private TransactionIdFilter sut;

    @Mock
    private GatewayFilterChain filterChain;

    @Test
    void filter_shouldAddTransactionIdHeaderAndCallChain() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/test").build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);
        ArgumentCaptor<ServerWebExchange> captor = ArgumentCaptor.forClass(ServerWebExchange.class);

        when(filterChain.filter(any(ServerWebExchange.class))).thenReturn(Mono.empty());

        sut.filter(exchange, filterChain).block();

        verify(filterChain).filter(captor.capture());
        ServerWebExchange mutatedExchange = captor.getValue();
        String headerValue = mutatedExchange.getRequest().getHeaders()
                .getFirst(TRANSACTION_ID_HEADER);
        assertThat(headerValue).isNotNull();
        assertThat(headerValue).matches(UUID_PATTERN);
        assertThat(MDC.get(TRANSACTION_ID)).isNull();
    }

    @Test
    void getOrder_shouldReturnMinus100() {
        assertThat(sut.getOrder()).isEqualTo(FILTER_ORDER);
    }
}