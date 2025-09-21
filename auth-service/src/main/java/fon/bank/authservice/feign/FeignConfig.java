package fon.bank.authservice.feign;

import feign.Logger;
import feign.RequestInterceptor;
import fon.bank.authservice.security.config.ServiceTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
@RequiredArgsConstructor
public class FeignConfig {
    private final ServiceTokenProvider serviceTokenProvider;

    @Bean
    public RequestInterceptor svcTokenInterceptor() {
        return template -> {
            String jwt = serviceTokenProvider.mintSvcToken();
            template.header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt);
        };
    }

    @Bean
    Logger.Level feignLoggerLevel() { return Logger.Level.FULL; }
}
