package fon.bank.userservice.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.web.SecurityFilterChain;

import org.springframework.core.convert.converter.Converter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/health").permitAll()
                        .requestMatchers(HttpMethod.GET, "/clients/lookup").permitAll()
                        .requestMatchers(HttpMethod.POST, "/clients/*/link-user").hasAuthority("SVC_AUTH")
                        .requestMatchers(HttpMethod.GET, "/users/clients").hasRole("EMPLOYEE")
                        .requestMatchers(HttpMethod.GET, "/internal/users/**").hasAuthority("SVC_AUTH")
                        .requestMatchers(HttpMethod.GET, "/users/employees").hasRole("EMPLOYEE")
                        .requestMatchers(HttpMethod.GET, "/users/clients/{username}").hasAnyRole("EMPLOYEE", "CLIENT")
                        .requestMatchers(HttpMethod.GET, "/users/employees/{username}").hasAnyRole("EMPLOYEE")
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth -> oauth
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthConverter()))
                );
        return http.build();

    }

    @Bean
    Converter<Jwt, ? extends AbstractAuthenticationToken> jwtAuthConverter() {
        var base = new org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter();
        base.setJwtGrantedAuthoritiesConverter((Jwt jwt) -> {
            Collection<GrantedAuthority> grants = new ArrayList<>();

            String role = jwt.getClaimAsString("role");
            if (role != null && !role.isBlank()) {
                String val = role.startsWith("ROLE_") ? role : "ROLE_" + role;
                grants.add(new SimpleGrantedAuthority(val));
            }
            List<String> authorities = jwt.getClaimAsStringList("authorities");
            if (authorities != null) {
                for (String a : authorities) {
                    if (a != null && !a.isBlank()) {
                        grants.add(new SimpleGrantedAuthority(a));
                    }
                }
            }

            return grants;
        });
        return base;
    }
}
