package fon.bank.authservice.security.config;

import fon.bank.authservice.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtEncoder encoder;   // RS256 potpisivanje
    private final JwtDecoder decoder;   // dekodiranje (za 2FA preAuth i interne provere)

    @Value("${security.jwt.issuer}")
    private String issuer;

    @Value("${jwt.access-token-ms:900000}")
    private long accessTokenMs;

    @Value("${jwt.refresh-token-ms:604800000}")
    private long refreshTokenMs;

    /* =========================
       Helpers za čitanje claim-ova
       ========================= */
    public String extractUsername(String token) {
        return decoder.decode(token).getSubject();
    }

    public <T> T extractClaim(String token, Function<Jwt, T> claimsResolver) {
        Jwt jwt = decoder.decode(token);
        return claimsResolver.apply(jwt);
    }

    public boolean isTokenExpired(String token) {
        Instant exp = decoder.decode(token).getExpiresAt();
        return exp != null && exp.isBefore(Instant.now());
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    public String extractUserType(String token) {
        return extractClaim(token, jwt -> jwt.getClaimAsString("role"));
    }

    /* =========================
       Generisanje tokena (RS256)
       ========================= */
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        if (userDetails instanceof User u) {
            extraClaims.put("role", u.getRole());  // npr. ROLE_CLIENT
        }
        return buildToken(extraClaims, userDetails.getUsername(), Duration.ofMillis(accessTokenMs));
    }

    public String generateRefreshToken(UserDetails userDetails) {
        return buildToken(new HashMap<>(), userDetails.getUsername(), Duration.ofMillis(refreshTokenMs));
    }

    private String buildToken(Map<String, Object> extraClaims, String subject, Duration ttl) {
        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(issuer)
                .issuedAt(now)
                .expiresAt(now.plus(ttl))
                .subject(subject)
                .claims(map -> map.putAll(extraClaims))
                .build();

        JwsHeader headers = JwsHeader.with(SignatureAlgorithm.RS256).type("JWT").build();
        return encoder.encode(JwtEncoderParameters.from(headers, claims)).getTokenValue();
    }

    /* =========================
       2FA Pre-Auth token (kratak rok)
       ========================= */
    public String generatePreAuthToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", user.getUsername());
        claims.put("2fa", true);
        claims.put("purpose", "LOGIN_2FA");

        // 3 min
        Duration ttl = Duration.ofMinutes(3);
        return buildToken(claims, user.getUsername(), ttl);
    }

    public String extractUsernameFromPreAuth(String token) {
        Jwt jwt = decoder.decode(token);
        String purpose = jwt.getClaimAsString("purpose");
        if (!"LOGIN_2FA".equals(purpose)) {
            throw new BadCredentialsException("Invalid preAuth token");
        }
        return jwt.getSubject();
    }
}
