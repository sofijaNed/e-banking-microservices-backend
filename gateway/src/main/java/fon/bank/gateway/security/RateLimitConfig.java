package fon.bank.gateway.security;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Configuration
public class RateLimitConfig {

    @Bean("principalOrIpKeyResolver")
    @Primary
    public KeyResolver principalOrIpKeyResolver() {
        return exchange -> exchange.getPrincipal()
                .filter(p -> p instanceof JwtAuthenticationToken)
                .cast(JwtAuthenticationToken.class)
                .map(JwtAuthenticationToken::getName)       // user id / username iz JWT-a
                .switchIfEmpty(Mono.just(clientIp(exchange)));
    }

    @Bean("ipKeyResolver")
    public KeyResolver ipKeyResolver() {
        return exchange -> Mono.just(clientIp(exchange));
    }

    private String clientIp(ServerWebExchange exchange) {
        String xf = exchange.getRequest().getHeaders().getFirst("X-Forwarded-For");
        if (xf != null && !xf.isBlank()) return xf.split(",")[0].trim();
        var ra = exchange.getRequest().getRemoteAddress();
        return (ra != null && ra.getAddress() != null) ? ra.getAddress().getHostAddress() : "unknown";
    }
}