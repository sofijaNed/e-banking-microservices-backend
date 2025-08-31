package fon.bank.gateway.security;

import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Configuration
public class RateLimitErrorHandler {

    @Bean
    public WebExceptionHandler tooManyRequestsHandler() {
        return (exchange, ex) -> {
            // Gateway stavlja atribut RATE_LIMITED kad limiter odbije zahtev
            var limited = exchange.getAttributeOrDefault(
                    ServerWebExchangeUtils.CIRCUITBREAKER_EXECUTION_EXCEPTION_ATTR, null);

            if (exchange.getResponse().getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
                var res = exchange.getResponse();
                res.getHeaders().setContentType(MediaType.APPLICATION_JSON);
                res.getHeaders().set("Retry-After", "2"); // ili izračunaj
                var body = """
          {"error":"too_many_requests","message":"Rate limit exceeded. Try again shortly."}
          """;
                DataBufferFactory bf = res.bufferFactory();
                return res.writeWith(Mono.just(bf.wrap(body.getBytes(StandardCharsets.UTF_8))));
            }
            return Mono.error(ex);
        };
    }
}
