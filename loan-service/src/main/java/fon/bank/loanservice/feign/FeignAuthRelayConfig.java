package fon.bank.loanservice.feign;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class FeignAuthRelayConfig {
    @Bean
    public RequestInterceptor correlationInterceptor(
            @Value("${app.correlation.header:X-Correlation-ID}") String header) {
        return template -> {
            String cid = MDC.get("cid");
            if (cid == null || cid.isBlank()) {
                var attrs = RequestContextHolder.getRequestAttributes();
                if (attrs instanceof ServletRequestAttributes sra) {
                    cid = sra.getRequest().getHeader(header);
                }
            }
            if (cid != null && !cid.isBlank()) {
                template.header(header, cid);
            }
        };
    }
    @Bean
    public RequestInterceptor bearerForwardingInterceptor() {
        return template -> {
            var attrs = RequestContextHolder.getRequestAttributes();
            if (attrs instanceof ServletRequestAttributes sra) {
                String auth = sra.getRequest().getHeader("Authorization");
                if (auth != null && !auth.isBlank()) {
                    template.header("Authorization", auth);
                    return;
                }
            }
            var auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth instanceof JwtAuthenticationToken jwt) {
                template.header("Authorization", "Bearer " + jwt.getToken().getTokenValue());
            }
        };
    }
}
