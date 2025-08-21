package fon.bank.authservice.security.auth;

import fon.bank.authservice.dao.UserRepository;
import fon.bank.authservice.dto.UserTokenInfoDTO;
import fon.bank.authservice.entity.User;
import fon.bank.authservice.security.config.JwtService;
import fon.bank.authservice.security.twofactorauth.OtpService;
import fon.bank.authservice.security.twofactorauth.VerifyOtpRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final JwtService jwtService;
    private final OtpService otpService;
    private final UserRepository userRepository;

    @Value("${jwt.refresh-cookie-name:refresh_token}")
    private String refreshCookieName;

    @Value("${jwt.refresh-cookie-path:/}")
    private String refreshCookiePath;

    @Value("${jwt.refresh-cookie-secure:false}")
    private boolean refreshCookieSecure;

    @Value("${jwt.refresh-token-ms:604800000}")
    private long refreshTokenMs;

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authentication(
            @Valid @RequestBody AuthenticationRequest request,
            HttpServletResponse response
    ) {
        // Service vrati access + (tranziciono) refresh u body-ju
        AuthenticationResponse auth = authenticationService.authenticate(request);

        // Ako NIJE 2FA korak, postavi refresh cookie i izbaci refresh iz body-ja
        if (!Boolean.TRUE.equals(auth.isTwoFactorRequired()) && auth.getRefreshToken() != null) {
            setRefreshCookie(response, auth.getRefreshToken());

            auth = AuthenticationResponse.builder()
                    .accessToken(auth.getAccessToken())
                    .username(auth.getUsername())
                    .role(auth.getRole())
                    .message(auth.getMessage())
                    .twoFactorRequired(false)
                    .build();
        }
        return ResponseEntity.ok(auth);
    }

    @GetMapping("/validate")
    public ResponseEntity<UserTokenInfoDTO> validateToken(@RequestHeader(value = "Authorization", required = false) String authz) {
        if (authz == null || !authz.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String jwt = authz.substring(7);
        if (jwtService.isTokenExpired(jwt)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String username = jwtService.extractUsername(jwt);
        String userType = jwtService.extractUserType(jwt);
        return ResponseEntity.ok(new UserTokenInfoDTO(username, userType));
    }



    @PostMapping("/refreshToken")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        authenticationService.refreshToken(request, response);
    }

    @PostMapping("/verify-otp")
    public AuthenticationResponse verifyOtp(@RequestBody VerifyOtpRequest req, HttpServletResponse response) {
        String username = jwtService.extractUsernameFromPreAuth(req.getPreAuthToken());
        if (!otpService.verifyOtp(username, req.getOtpCode(), "LOGIN_2FA")) {
            throw new BadCredentialsException("Invalid OTP");
        }
        User user = userRepository.findByUsername(username).orElseThrow();

        // completeAuthentication vraća access+refresh; ovde takođe setujemo cookie i krijemo refresh iz body-ja
        AuthenticationResponse auth = authenticationService.completeAuthentication(user);
        if (auth.getRefreshToken() != null) {
            setRefreshCookie(response, auth.getRefreshToken());
            auth = AuthenticationResponse.builder()
                    .accessToken(auth.getAccessToken())
                    .username(auth.getUsername())
                    .role(auth.getRole())
                    .message(auth.getMessage())
                    .twoFactorRequired(false)
                    .build();
        }
        return auth;
    }

    @GetMapping("/me")
    public ResponseEntity<UserTokenInfoDTO> me(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth) {
        if (auth == null || !auth.startsWith("Bearer ")) return ResponseEntity.status(401).build();
        String jwt = auth.substring(7);
        if (jwtService.isTokenExpired(jwt)) return ResponseEntity.status(401).build();
        String username = jwtService.extractUsername(jwt);
        String role = jwtService.extractUserType(jwt);
        return ResponseEntity.ok(new UserTokenInfoDTO(username, role));
    }

    @GetMapping("/csrf")
    public CsrfToken csrf(CsrfToken token) { return token; }


    private void setRefreshCookie(HttpServletResponse response, String rawRefresh) {
        // prvo pobriši moguće prethodne (različitih path-ova)
        deleteRefreshCookie(response, "/auth");
        if (!"/".equals(refreshCookiePath)) {
            deleteRefreshCookie(response, "/");
        }

        ResponseCookie cookie = ResponseCookie.from(refreshCookieName, rawRefresh)
                .httpOnly(true)
                .secure(refreshCookieSecure)   // DEV=false, PROD=true (HTTPS)
                .sameSite("Strict")
                .path(refreshCookiePath)       // "/" u DEV, po želji "/auth" ili path gateway-a u PROD
                .maxAge(Duration.ofMillis(refreshTokenMs))
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    private void deleteRefreshCookie(HttpServletResponse response, String path) {
        ResponseCookie del = ResponseCookie.from(refreshCookieName, "")
                .httpOnly(true)
                .secure(refreshCookieSecure)
                .sameSite("Strict")
                .path(path)
                .maxAge(0)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, del.toString());
    }

}
