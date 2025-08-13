package fon.bank.authservice.security.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "security.jwt")
public class JwtProperties {
    private String issuer;
    private Key key = new Key();

    @Data
    public static class Key {
        private String id;
        private String privatePemPath;
        private String publicPemPath;
    }
}
