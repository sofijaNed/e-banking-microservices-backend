package fon.bank.gateway.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(ex -> ex
                        .pathMatchers("/auth/**", "/.well-known/jwks.json", "/actuator/health").permitAll()
                        // Sve ostalo MORA imati validan JWT
                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(oauth -> oauth
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthConverter()))
                )
                .build();
    }

    private static org.springframework.core.convert.converter.Converter<Jwt, Mono<JwtAuthenticationToken>> jwtAuthConverter() {
        return jwt -> {
            List<GrantedAuthority> auths = new ArrayList<>();
            String role = jwt.getClaimAsString("role");
            if (role != null && !role.isBlank()) {
                auths.add(new SimpleGrantedAuthority(role));
            }
            return Mono.just(new JwtAuthenticationToken(jwt, auths));
        };
    }
}
