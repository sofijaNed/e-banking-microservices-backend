package fon.bank.authservice.security.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import fon.bank.authservice.security.twofactorauth.OtpService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
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


    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(),request.getPassword()));

        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(()->new BadCredentialsException("Data is not valid."));

        if (user.getTwoFactorEnabled() && Boolean.TRUE.equals(request.isUse2fa())) {
            String preAuthToken = jwtService.generatePreAuthToken(user);

            otpService.generateAndSendOtp(user, request.getEmail(), "LOGIN_2FA");

            return AuthenticationResponse.builder()
                    .twoFactorRequired(true)
                    .preAuthToken(preAuthToken)
                    .message("OTP poslat na email")
                    .build();
        }
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        revokeAllMemberTokens(user);
        saveMemberToken(user,jwtToken);

        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .username(user.getUsername())
                .role(user.getRole().name())
                .message("Succesfull logging.")
                .build();
    }


    public AuthenticationResponse completeAuthentication(User user) {
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        revokeAllMemberTokens(user);
        saveMemberToken(user, jwtToken);


    return AuthenticationResponse.builder()
            .accessToken(jwtToken)
            .refreshToken(refreshToken)
            .username(user.getUsername())
            .twoFactorRequired(false)
            .role(user.getRole().name())
            .message("Successfully verified OTP")
            .build();
    }



    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println("In refresh token method.");

        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String userUsername;

        // Check for Bearer token in the Authorization header
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return; // No token present
        }

        refreshToken = authHeader.substring(7); // Extract refresh token
        userUsername = jwtService.extractUsername(refreshToken);

        if (userUsername == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        // If username is present and the token is valid
        var user = userRepository.findByUsername(userUsername)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        var storedRefreshOpt = tokenRepository.findByToken(refreshToken);
        if (storedRefreshOpt.isEmpty() || storedRefreshOpt.get().isRevoked() || storedRefreshOpt.get().isExpired()) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // 2) Ensure signature and expiry valid
        if (!jwtService.isTokenValid(refreshToken, user)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        var storedRefresh = storedRefreshOpt.get();
        storedRefresh.setRevoked(true);
        storedRefresh.setExpired(true);
        tokenRepository.save(storedRefresh);

        // 4) Create new access and new refresh token
        var newAccessToken = jwtService.generateToken(user);
        var newRefreshToken = jwtService.generateRefreshToken(user);

        saveMemberToken(user, newAccessToken); // access token saved as before
        // save new refresh token with TokenType.REFRESH (or the same enum if extended)
        var refreshTokenEntity = Token.builder()
                .token(newRefreshToken)
                .user(user)
                .tokenType(TokenType.BEARER) // ideally REFRESH if you add it
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(refreshTokenEntity);

        var authResponse = AuthenticationResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
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


    public void saveMemberToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }
}
