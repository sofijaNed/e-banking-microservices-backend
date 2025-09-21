package fon.bank.authservice.security.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.stereotype.Service;
import fon.bank.authservice.dao.UserRepository;
import fon.bank.authservice.entity.User;
import fon.bank.authservice.security.config.JwtService;
import fon.bank.authservice.security.token.Token;
import fon.bank.authservice.security.token.TokenRepository;
import fon.bank.authservice.security.token.TokenType;

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

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(),request.getPassword()));

        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(()->new BadCredentialsException("Data is not valid."));

        if (user.getTwoFactorEnabled() && Boolean.TRUE.equals(request.isUse2fa())) {
            String preAuthToken = jwtService.generatePreAuthToken(user);

            String email = directoryService.resolveEmailForUser(user);
            otpService.generateAndSendOtp(user, email, "LOGIN_2FA");

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
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // 2) mora biti typ=refresh i neistekao
        try {
            String typ = jwtService.extractTyp(rawRefresh);
            if (!"refresh".equals(typ) || jwtService.isTokenExpired(rawRefresh)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String username = jwtService.extractUsername(rawRefresh);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String hash = sha256(refreshPepper + rawRefresh);
        var storedOpt = tokenRepository.findByToken(hash);
        if (storedOpt.isEmpty() || storedOpt.get().isRevoked() || storedOpt.get().isExpired()) {
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
        // Ako si dodala TokenType.REFRESH u enum – koristi ga; u suprotnom, ostani na BEARER bez menjanja šeme.
        try {
            TokenType.valueOf("REFRESH");
            return true;
        } catch (Exception ignore) {
            return false;
        }
    }

}
