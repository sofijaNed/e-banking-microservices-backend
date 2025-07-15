package fon.bank.authservice.security;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
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


    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .cors(corsCustomizer -> corsCustomizer.configurationSource(new CorsConfigurationSource() {

                    @Override
                    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                        System.out.println("Proverava konfiguraciju server");
                        CorsConfiguration config = new CorsConfiguration();
                        config.setAllowedOrigins(List.of("http://localhost:4200"));
                        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                        config.setAllowCredentials(true);
                        config.setAllowedHeaders(List.of("*"));
                        config.setExposedHeaders(List.of("Authorization"));
                        config.setMaxAge(3600L);
                        return config;
                    }
                })).csrf((csrf) -> csrf.disable())
                .authorizeHttpRequests((requests) -> requests
//                        .requestMatchers("**").permitAll())
                        .requestMatchers(HttpMethod.POST, "/transactions/savePliz").hasAnyRole("CLIENT", "EMPLOYEE")
                        .requestMatchers(HttpMethod.GET, "/transactions").hasAnyRole("CLIENT", "EMPLOYEE")
                        .requestMatchers(HttpMethod.GET, "/transactions/{id}").hasAnyRole("CLIENT", "EMPLOYEE")
                        .requestMatchers(HttpMethod.POST, "/accounts/**", "/clients/**", "/loans/**").hasAnyRole("CLIENT", "EMPLOYEE")
                        .requestMatchers(HttpMethod.GET, "/accounts/**").hasAnyRole("CLIENT", "EMPLOYEE")
                        .requestMatchers(HttpMethod.GET, "/clients/**").hasAnyRole("CLIENT", "EMPLOYEE")
                        .requestMatchers(HttpMethod.GET, "/clients/{id}/accounts").hasAnyRole("CLIENT", "EMPLOYEE")
                        .requestMatchers(HttpMethod.GET, "/clients/{id}").hasAnyRole("CLIENT", "EMPLOYEE")
                        .requestMatchers(HttpMethod.GET, "/loans/**").hasRole("EMPLOYEE")
                        .requestMatchers("/auth/**").permitAll())
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .logout(logoutConfigurer -> logoutConfigurer
                        .logoutUrl("/auth/logout")
                        .addLogoutHandler(logoutHandler)
                        .logoutSuccessHandler((request, response, authentication) -> SecurityContextHolder.clearContext())
                );
        return http.build();
    }

//    @Bean
//    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception{
//        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                .cors(corsCustomizer -> corsCustomizer.configurationSource(new CorsConfigurationSource() {
//
//                    @Override
//                    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
//                        System.out.println("Proverava konfiguraciju server");
//                        CorsConfiguration config = new CorsConfiguration();
//                        config.setAllowedOrigins(Collections.singletonList("http://localhost:4200"));
//                        config.setAllowedMethods(Collections.singletonList("*"));
//                        config.setAllowCredentials(true);
//                        config.setAllowedHeaders(Collections.singletonList("*"));
//                        config.setExposedHeaders(List.of("Authorization"));
//                        config.setMaxAge(3600L);
//                        return config;
//                    }
//                })).csrf((csrf) -> csrf.disable())
//                .authorizeHttpRequests((requests)->requests
//                        .requestMatchers(HttpMethod.POST,"/transactions/plizSave").hasAnyRole("CLIENT")
//                        .requestMatchers(HttpMethod.GET,"/transactions").hasAnyRole("CLIENT","EMPLOYEE")
//                        .requestMatchers(HttpMethod.GET,"/transactions/{id}").hasAnyRole("CLIENT","EMPLOYEE")
//                        .requestMatchers(HttpMethod.POST,"/accounts/**","/clients/**",
//                                "/loans/**").hasAnyRole("CLIENT","EMPLOYEE")
////                        .requestMatchers(HttpMethod.DELETE,"/answers/**","/questions/**",
////                                "/exams/**","/professors/**","/students/**","/tests/**").hasRole("ADMIN")
//                        .requestMatchers(HttpMethod.GET,"/accounts/**").hasAnyRole("CLIENT","EMPLOYEE")
//                        .requestMatchers(HttpMethod.GET,"/clients/**").hasAnyRole("CLIENT","EMPLOYEE")
//                        .requestMatchers(HttpMethod.GET,"/clients/{id}/accounts").hasAnyRole("CLIENT","EMPLOYEE")
//                        .requestMatchers(HttpMethod.GET,"/clients/{id}").hasAnyRole("CLIENT","EMPLOYEE")
//                        .requestMatchers(HttpMethod.GET,"/loans/**").hasRole("EMPLOYEE")
//                        .requestMatchers("/auth/**").permitAll())
//                .authenticationProvider(authenticationProvider)
//                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
//                .logout(logoutConfigurer -> logoutConfigurer
//                        .logoutUrl("/auth/logout")
//                        .addLogoutHandler(logoutHandler)
//                        .logoutSuccessHandler((request, response, authentication) -> SecurityContextHolder.clearContext())
//                );
//        return http.build();
//    }
}
