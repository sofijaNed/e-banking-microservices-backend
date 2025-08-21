package fon.bank.authservice.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import fon.bank.authservice.security.config.JwtService;
import fon.bank.authservice.security.token.TokenRepository;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {


    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    // private final TokenRepository tokenRepository; // STATeless: uklonjeno

    private static final RequestMatcher PUBLIC = new OrRequestMatcher(
            new AntPathRequestMatcher("/.well-known/**"),
            new AntPathRequestMatcher("/auth/csrf", "GET"),
            new AntPathRequestMatcher("/auth/authenticate", "POST"),
            new AntPathRequestMatcher("/auth/verify-otp", "POST"),
            new AntPathRequestMatcher("/auth/refreshToken", "POST"),
            new AntPathRequestMatcher("/auth/logout", "POST")
            // NAMERNO nema /auth/me
    );

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        // 1) Ne diramo javne rute
        if (PUBLIC.matches(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2) Izvuci Authorization header
        final String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        final String jwt = authHeader.substring(7);
        final String username = jwtService.extractUsername(jwt);

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            // 3) STATeless validacija: potpis + exp iz JWT-a
            boolean valid = jwtService.isTokenValid(jwt, userDetails);

            // Ako želiš „optional blacklist“, ostavi samo provere „revoked“:
            // boolean notRevoked = tokenRepository.findByToken(jwt).map(t -> !t.isRevoked()).orElse(true);
            // boolean valid = jwtService.isTokenValid(jwt, userDetails) && notRevoked;

            if (valid) {
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}
