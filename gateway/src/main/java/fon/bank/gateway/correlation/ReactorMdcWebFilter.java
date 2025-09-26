package fon.bank.gateway.correlation;

import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class ReactorMdcWebFilter implements WebFilter {

    @Value("${app.correlation.header:X-Correlation-ID}")
    private String header;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String cid = exchange.getRequest().getHeaders().getFirst(header);
        if (cid == null || cid.isBlank()) return chain.filter(exchange);

        return chain.filter(exchange)
                .contextWrite(ctx -> ctx.put("cid", cid))
                .doOnEach(sig -> {
                    if (sig.isOnNext() || sig.isOnComplete()) {
                        MDC.put("cid", cid);
                    }
                })
                .doFinally(st -> MDC.remove("cid"));
    }
}
