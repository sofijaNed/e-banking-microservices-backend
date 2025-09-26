package fon.bank.authservice.security.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import fon.bank.authservice.dto.AuditEventDTO;
import fon.bank.authservice.feign.AuditPublisher;
import fon.bank.authservice.feign.DirectoryService;
import fon.bank.authservice.security.twofactorauth.OtpService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import fon.bank.authservice.dao.UserRepository;
import fon.bank.authservice.entity.User;
import fon.bank.authservice.security.config.JwtService;
import fon.bank.authservice.security.token.Token;
import fon.bank.authservice.security.token.TokenRepository;
import fon.bank.authservice.security.token.TokenType;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.util.Date;
import java.util.HexFormat;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final ModelMapper modelMapper;
    private final OtpService otpService;
    private final DirectoryService directoryService;
    private final AuditPublisher  auditPublisher;

    @Value("${jwt.refresh-token-ms:604800000}")
    private long refreshTokenMs;

    @Value("${jwt.refresh-cookie-name:refresh_token}")
    private String refreshCookieName;

    @Value("${jwt.refresh-cookie-path:/}")
    private String refreshCookiePath;

    @Value("${jwt.refresh-cookie-secure:false}")
    private boolean refreshCookieSecure;

    @Value("${jwt.refresh-pepper:pepper-change-me}")
    private String refreshPepper;

    @Value("${app.correlation.header:X-Correlation-ID}")
    private String correlationHeader;

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        long t0 = System.nanoTime();
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
        } catch (Exception ex) {
            int durMs = (int)((System.nanoTime() - t0) / 1_000_000);

            audit("LOGIN", "FAIL", request.getUsername(), 401, durMs,
                    "{\"reason\":\"bad_credentials\"}",
                    "{\"sanitized\":true,\"contains_pii\":false,\"password_plaintext\":false}");
            throw ex;
        }

        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(()-> {
                    int durMs = (int)((System.nanoTime() - t0) / 1_000_000);
                    audit("LOGIN", "FAIL", request.getUsername(), 401, durMs,
                            "{\"reason\":\"user_not_found\"}", null);
                    return new BadCredentialsException("Podaci nisu validni.");
                });


        if (user.getTwoFactorEnabled() && Boolean.TRUE.equals(request.isUse2fa())) {
            String preAuthToken = jwtService.generatePreAuthToken(user);

            String email = directoryService.resolveEmailForUser(user);
            otpService.generateAndSendOtp(user, email, "LOGIN_2FA");

            int durMs = (int)((System.nanoTime() - t0) / 1_000_000);
            audit("LOGIN_2FA_SENT", "SUCCESS", request.getUsername(), 200, durMs,
                    "{\"method\":\"EMAIL\"}",
                    "{\"otp_in_payload\":false,\"sanitized\":true}");


            return AuthenticationResponse.builder()
                    .twoFactorRequired(true)
                    .preAuthToken(preAuthToken)
                    .message("OTP poslat na email")
                    .build();
        }
        String access  = jwtService.generateAccessToken(user);
        String refresh = jwtService.generateRefreshToken(user);
        revokeAllMemberTokens(user);
        saveMemberToken(user,refresh);

        int durMs = (int)((System.nanoTime() - t0) / 1_000_000);
        audit("LOGIN", "SUCCESS", request.getUsername(), 200, durMs,
                "{\"method\":\"password\"}",
                "{\"sanitized\":true,\"contains_pii\":false,\"otp_in_payload\":false}");


        return AuthenticationResponse.builder()
                .accessToken(access)
                .refreshToken(refresh)
                .username(user.getUsername())
                .role(user.getRole().name())
                .message("Succesfull logging.")
                .build();
    }


    public AuthenticationResponse completeAuthentication(User user) {
        String access  = jwtService.generateAccessToken(user);
        String refresh = jwtService.generateRefreshToken(user);

        revokeAllMemberTokens(user);
        saveMemberToken(user,refresh);

        audit("LOGIN_2FA_VERIFY", "SUCCESS", user.getUsername(), 200, null,
                "{\"method\":\"otp\"}", null);

    return AuthenticationResponse.builder()
            .accessToken(access)
            .refreshToken(refresh)
            .username(user.getUsername())
            .twoFactorRequired(false)
            .role(user.getRole().name())
            .message("Successfully verified OTP")
            .build();
    }



    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println("In refresh token method.");

        String rawRefresh = readLatestValidRefresh(request);
        if (rawRefresh == null) {
            audit("REFRESH_TOKEN","FAIL",(String)null,401,null,
                    "{\"reason\":\"no_valid_cookie\"}",null);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        try {
            String typ = jwtService.extractTyp(rawRefresh);
            if (!"refresh".equals(typ) || jwtService.isTokenExpired(rawRefresh)) {
                audit("REFRESH_TOKEN","FAIL",(String)null,401,null,
                        "{\"reason\":\"invalid_typ_or_expired\"}",null);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        } catch (Exception e) {
            audit("REFRESH_TOKEN","FAIL",(String)null,401,null,
                    "{\"reason\":\"parse_error\"}",null);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String username = jwtService.extractUsername(rawRefresh);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Korisnik nije pronadjen."));

        String hash = sha256(refreshPepper + rawRefresh);
        var storedOpt = tokenRepository.findByToken(hash);
        if (storedOpt.isEmpty() || storedOpt.get().isRevoked() || storedOpt.get().isExpired()) {
            audit("REFRESH_TOKEN","FAIL", username, 401, null,
                    "{\"reason\":\"not_found_or_revoked\"}", null);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        Token stored = storedOpt.get();
        stored.setRevoked(true);
        stored.setExpired(true);
        tokenRepository.save(stored);

        String newAccess  = jwtService.generateAccessToken(user);
        String newRefresh = jwtService.generateRefreshToken(user);

        revokeAllMemberTokens(user);
        saveMemberToken(user,newRefresh);

        setRefreshCookie(response, newRefresh);
        audit("REFRESH_TOKEN","SUCCESS", user.getUsername(), 200, null,
                "{\"rotated\":true}", null);
        var authResponse = AuthenticationResponse.builder()
                .accessToken(newAccess)
                .message("refreshed")
                .build();

        response.setStatus(HttpServletResponse.SC_OK);
        new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
    }


    public void revokeAllMemberTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getUsername());
        System.out.println(validUserTokens);
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }


    public void saveMemberToken(User user, String rawRefresh) {
        var token = Token.builder()
                .user(user)
                .token(sha256(refreshPepper + rawRefresh))
                .tokenType(supportsRefreshEnum() ? TokenType.REFRESH : TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

    public void setRefreshCookie(HttpServletResponse response, String rawRefresh) {
        deleteRefreshCookie(response, "/auth");
        if (!"/".equals(refreshCookiePath)) deleteRefreshCookie(response, "/");

        ResponseCookie cookie = ResponseCookie.from(refreshCookieName, rawRefresh)
                .httpOnly(true)
                .secure(refreshCookieSecure)
                .sameSite("Lax")
                .path(refreshCookiePath)
                .maxAge(Duration.ofMillis(refreshTokenMs))
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    public void deleteRefreshCookie(HttpServletResponse response, String path) {
        ResponseCookie del = ResponseCookie.from(refreshCookieName, "")
                .httpOnly(true)
                .secure(refreshCookieSecure)
                .sameSite("Strict")
                .path(path)
                .maxAge(0)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, del.toString());
    }

    private String readLatestValidRefresh(HttpServletRequest req) {
        Cookie[] cs = Optional.ofNullable(req.getCookies()).orElse(new Cookie[0]);
        String best = null;
        Date bestIat = null;

        for (Cookie c : cs) {
            if (!refreshCookieName.equals(c.getName())) continue;
            String val = c.getValue();
            try {
                if (!"refresh".equals(jwtService.extractTyp(val))) continue;
                if (jwtService.isTokenExpired(val)) continue;
                Date iat = Date.from(Optional.ofNullable(jwtService.extractIssuedAt(val)).orElseThrow());
                if (best == null || (iat != null && (bestIat == null || iat.after(bestIat)))) {
                    best = val;
                    bestIat = iat;
                }
            } catch (Exception ignored) {}
        }
        return best;
    }

    private String sha256(String s) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(md.digest(s.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }

    private boolean supportsRefreshEnum() {
        try {
            TokenType.valueOf("REFRESH");
            return true;
        } catch (Exception ignore) {
            return false;
        }
    }

    private HttpServletRequest currentRequest() {
        var attrs = RequestContextHolder.getRequestAttributes();
        return (attrs instanceof ServletRequestAttributes sra) ? sra.getRequest() : null;
    }
    private String clientIp() {
        var r = currentRequest();
        if (r == null) return null;
        String fwd = r.getHeader("X-Forwarded-For");
        return (fwd != null && !fwd.isBlank()) ? fwd.split(",")[0].trim() : r.getRemoteAddr();
    }
    private String userAgent() {
        var r = currentRequest();
        return (r == null) ? null : r.getHeader("User-Agent");
    }
    private String correlationId() {
        String cid = org.slf4j.MDC.get("cid");
        if (cid != null && !cid.isBlank()) return cid;

        var r = currentRequest();
        if (r != null) {
            cid = r.getHeader(correlationHeader);
            if (cid != null && !cid.isBlank()) return cid;
        }

        return java.util.UUID.randomUUID().toString();
    }

    private void audit(String action, String outcome, String username,
                       Integer httpStatus, Integer durationMs,
                       String detailsJson, String checksJson) {
        AuditEventDTO ev = new AuditEventDTO();
        ev.setService("auth-service");
        ev.setAction(action);
        ev.setOutcome(outcome);
        ev.setPrincipal(username);
        ev.setIp(clientIp());
        ev.setUserAgent(userAgent());
        ev.setResourceType("USER");
        ev.setResourceId(username);
        ev.setCorrelationId(correlationId());
        var req = currentRequest();
        if (req != null) {
            ev.setHttpMethod(req.getMethod());
            ev.setHttpPath(req.getRequestURI());
        }
        if (httpStatus != null) ev.setHttpStatus(httpStatus);
        if (durationMs != null) ev.setDurationMs(durationMs);
        if (detailsJson != null) ev.setDetailsJson(detailsJson);
        if (checksJson != null) ev.setChecksJson(checksJson);

        try { auditPublisher.send(ev); } catch (Exception ignore) {  }
    }

}
