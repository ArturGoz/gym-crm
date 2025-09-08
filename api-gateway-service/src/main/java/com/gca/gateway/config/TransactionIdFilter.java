package com.gca.gateway.config;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@Component
public class TransactionIdFilter implements GlobalFilter, Ordered {

    private static final String TRANSACTION_ID = "transactionId";
    private static final String TRANSACTION_ID_HEADER = "X-Transaction-Id";
    private static final int FILTER_ORDER = -100;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String transactionId = UUID.randomUUID().toString();
        ServerWebExchange mutatedExchange = addTransactionIdToRequest(exchange, transactionId);

        MDC.put(TRANSACTION_ID, transactionId);

        return chain.filter(mutatedExchange)
                .doFinally(signalType -> MDC.remove(TRANSACTION_ID));
    }

    @Override
    public int getOrder() {
        return FILTER_ORDER;
    }

    private ServerWebExchange addTransactionIdToRequest(ServerWebExchange exchange, String transactionId) {
        log.info("Adding Transaction ID [{}] to request URI [{}]",
                transactionId, exchange.getRequest().getURI());

        return exchange.mutate()
                .request(exchange.getRequest().mutate()
                        .header(TRANSACTION_ID_HEADER, transactionId)
                        .build())
                .build();
    }
}
