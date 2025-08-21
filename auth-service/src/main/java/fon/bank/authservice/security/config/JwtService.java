package fon.bank.authservice.security.config;

import fon.bank.authservice.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
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

    public String generateAccessToken(UserDetails user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("typ", "access");
        claims.put("jti", UUID.randomUUID().toString());
        addRoleClaims(claims, user);
        return buildToken(claims, user.getUsername(), Duration.ofMillis(accessTokenMs));
    }

    public String generateRefreshToken(UserDetails user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("typ", "refresh");
        claims.put("jti", UUID.randomUUID().toString());
        claims.put("ori", System.currentTimeMillis());
        return buildToken(claims, user.getUsername(), Duration.ofMillis(refreshTokenMs));
    }

    public String generateRefreshToken(UserDetails user, Instant originalIat) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("typ", "refresh");
        claims.put("jti", UUID.randomUUID().toString());
        claims.put("ori", originalIat == null ? System.currentTimeMillis() : originalIat.toEpochMilli());
        return buildToken(claims, user.getUsername(), Duration.ofMillis(refreshTokenMs));
    }

    public String extractUsername(String token) {
        return decoder.decode(token).getSubject();
    }

    public String extractTyp(String token) {
        return extractClaim(token, jwt -> jwt.getClaimAsString("typ"));
    }

    public String extractJti(String token) {
        return extractClaim(token, jwt -> jwt.getClaimAsString("jti"));
    }

    public Instant extractIssuedAt(String token) {
        return decoder.decode(token).getIssuedAt();
    }

    public String extractUserRole(String token) {
        return extractClaim(token, jwt -> jwt.getClaimAsString("role"));
    }

    public Instant extractOriginalIat(String token) {
        Long oriMs = extractClaim(token, jwt -> {
            Object v = jwt.getClaims().get("ori");
            if (v instanceof Number n) return n.longValue();
            return null;
        });
        return (oriMs == null) ? null : Instant.ofEpochMilli(oriMs);
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

//    public String generateRefreshToken(UserDetails userDetails) {
//        return buildToken(new HashMap<>(), userDetails.getUsername(), Duration.ofMillis(refreshTokenMs));
//    }

    private String buildToken(Map<String, Object> extraClaims, String subject, Duration ttl) {
        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(issuer)
                .issuedAt(now)
                .expiresAt(now.plus(ttl))
                .subject(subject)
                .claims(map -> map.putAll(extraClaims))
                .build();

        // RS256 header (kid će dodati encoder/KeyManager ako je podešen)
        JwsHeader headers = JwsHeader.with(SignatureAlgorithm.RS256).type("JWT").build();
        return encoder.encode(JwtEncoderParameters.from(headers, claims)).getTokenValue();
    }

    private void addRoleClaims(Map<String, Object> claims, UserDetails user) {
        // 1) Jednostavno: jedan "role" claim (ROLE_CLIENT / ROLE_EMPLOYEE)
        if (user instanceof User u) {
            claims.put("role", u.getRole()); // npr. "ROLE_CLIENT"
        } else {
            // ili iz authorities
            Optional<String> firstRole = user.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority).findFirst();
            firstRole.ifPresent(r -> claims.put("role", r));
        }

        // 2) Ako želiš i listu: claims.put("roles", List<String> authorities));
        //   pa u resource-servisima mapirati na GrantedAuthority-e.
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
