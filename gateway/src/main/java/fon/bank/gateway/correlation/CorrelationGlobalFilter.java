package fon.bank.gateway.correlation;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.UUID;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorrelationGlobalFilter implements GlobalFilter {

    @Value("${app.correlation.header:X-Correlation-ID}")
    private String header;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String cid = Optional.ofNullable(exchange.getRequest().getHeaders().getFirst(header))
                .filter(s -> !s.isBlank())
                .orElse(UUID.randomUUID().toString());

        ServerHttpRequest mutated = exchange.getRequest().mutate()
                .headers(h -> h.set(header, cid))
                .build();

        exchange.getResponse().getHeaders().set(header, cid);

        return chain.filter(exchange.mutate().request(mutated).build());
    }
}
