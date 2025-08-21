package fon.bank.authservice.security;

import fon.bank.authservice.security.filter.CsrfCookieFilter;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import fon.bank.authservice.security.filter.JwtAuthenticationFilter;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {


    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;
    private final LogoutHandler logoutHandler;

    @Value("${jwt.refresh-cookie-name:refresh_token}")
    private String refreshCookieName;

    @Value("${jwt.refresh-cookie-path:/}")
    private String refreshCookiePath;

    @Value("${jwt.refresh-cookie-secure:false}") // PROD: true
    private boolean refreshCookieSecure;

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        CookieCsrfTokenRepository csrfRepo = CookieCsrfTokenRepository.withHttpOnlyFalse();
        csrfRepo.setCookieName("XSRF-TOKEN");     // cookie koji čita Angular
        csrfRepo.setHeaderName("X-XSRF-TOKEN");   // header koji šalje Angular
        csrfRepo.setCookiePath("/");              // važi globalno
        csrfRepo.setSecure(false);                // PROD: true
        csrfRepo.setCookieCustomizer(c -> c.sameSite("Lax")); // DEV: Lax (prod: None + Secure)

        // 2) OBAVEZNO poveži handler (bez XOR dekodiranja)
        CsrfTokenRequestAttributeHandler requestHandler = new CsrfTokenRequestAttributeHandler();
        requestHandler.setCsrfRequestAttributeName("_csrf");

        http
                .csrf(csrf -> csrf
                        .csrfTokenRepository(csrfRepo)
                        .csrfTokenRequestHandler(requestHandler)
                        // CSRF je isključen samo za rute gde nam ne treba (npr. login, otp, sam csrf endpoint)
                        .ignoringRequestMatchers(
                                new AntPathRequestMatcher("/auth/authenticate", "POST"),
                                new AntPathRequestMatcher("/auth/verify-otp",    "POST"),
                                new AntPathRequestMatcher("/auth/csrf",          "GET")
                                // NAPOMENA: refreshToken i logout NISU ignorisani — ostaju pod CSRF zaštitom
                        )
                )
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(req -> req
                        // Ne koristi /auth/** globalno, već nabroji tačno
                        .requestMatchers(HttpMethod.GET, "/.well-known/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/auth/csrf").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/authenticate", "/auth/verify-otp").permitAll()
                        // refresh/logout mogu biti permitAll (jer nema access tokena), ali CSRF ih štiti
                        .requestMatchers(HttpMethod.POST, "/auth/refreshToken", "/auth/logout").permitAll()
                        .requestMatchers(HttpMethod.GET, "/auth/me").authenticated()
                        .anyRequest().authenticated()
                )
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .logout(logout -> logout
                        .logoutUrl("/auth/logout")
                        .addLogoutHandler(logoutHandler)
                        .logoutSuccessHandler((request, response, authentication) -> {
                            SecurityContextHolder.clearContext();
                            ResponseCookie del = ResponseCookie.from(refreshCookieName, "")
                                    .httpOnly(true)
                                    .secure(refreshCookieSecure)     // PROD: true
                                    .sameSite("Lax")                 // treba da se poklopi sa set-om
                                    .path(refreshCookiePath)         // isti path kao pri setovanju
                                    .maxAge(0)
                                    .build();
                            response.addHeader(HttpHeaders.SET_COOKIE, del.toString());
                        })
                );

        // 3) Ovaj filter SAMO re-iznosi već postojeći token u cookie (ne pravi novi)
        http.addFilterAfter(new CsrfCookieFilter(), CsrfFilter.class);

        // .cors() preskačemo ako ide preko gateway/proxy-ja (kao što si i napisao)
        return http.build();
    }

}
