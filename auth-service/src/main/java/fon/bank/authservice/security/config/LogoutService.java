package fon.bank.authservice.security.config;

import fon.bank.authservice.security.token.TokenType;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;
import fon.bank.authservice.security.token.TokenRepository;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.HexFormat;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler {


    private final TokenRepository tokenRepository;

    @Value("${jwt.refresh-cookie-name:refresh_token}")
    private String refreshCookieName;

    @Value("${jwt.refresh-cookie-path:/}")
    private String refreshCookiePath;

    @Value("${jwt.refresh-cookie-secure:false}")
    private boolean refreshCookieSecure;

    @Value("${jwt.refresh-pepper:pepper-change-me}")
    private String refreshPepper;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        // 1) Uzmi RAW refresh iz kolačića (ne iz Authorization zaglavlja)
        String rawRefresh = readCookie(request, refreshCookieName);

        if (rawRefresh != null && !rawRefresh.isBlank()) {
            try {
                // 2) Heširaj (isti algoritam kao pri upisu)
                String hash = sha256(refreshPepper + rawRefresh);

                // 3) Nađi token u bazi i opozovi ga
                tokenRepository.findByToken(hash).ifPresent(t -> {
                    // Ako imaš REFRESH tip, dodatno proveri
                    if (t.getTokenType() == null || t.getTokenType() == TokenType.REFRESH) {
                        t.setRevoked(true);
                        t.setExpired(true);
                        tokenRepository.save(t);
                    }
                });
            } catch (Exception ignored) {
                // ne dižemo 500 na logout
            }
        }

        // 4) Obriši refresh cookie (isti name/path/secure kao kad ga postavljaš)
        ResponseCookie del = ResponseCookie.from(refreshCookieName, "")
                .httpOnly(true)
                .secure(refreshCookieSecure)
                .sameSite("Strict")
                .path(refreshCookiePath) // obično "/"
                .maxAge(0)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, del.toString());

        // Ako si ranije imao cookie i na "/auth", obriši i njega (za svaki slučaj)
        if (!"/auth".equals(refreshCookiePath)) {
            ResponseCookie delLegacy = ResponseCookie.from(refreshCookieName, "")
                    .httpOnly(true).secure(refreshCookieSecure).sameSite("Strict")
                    .path("/auth").maxAge(0).build();
            response.addHeader(HttpHeaders.SET_COOKIE, delLegacy.toString());
        }

        // 5) Počisti security kontekst
        SecurityContextHolder.clearContext();
    }

    private String readCookie(HttpServletRequest req, String name) {
        Cookie[] cs = Optional.ofNullable(req.getCookies()).orElse(new Cookie[0]);
        return Arrays.stream(cs)
                .filter(c -> name.equals(c.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }

    private String sha256(String s) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        return HexFormat.of().formatHex(md.digest(s.getBytes(StandardCharsets.UTF_8)));
    }
}
