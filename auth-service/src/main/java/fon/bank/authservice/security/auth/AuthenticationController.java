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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final JwtService jwtService;
    private final OtpService otpService;
    private final UserRepository userRepository;


    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authentication(@Valid @RequestBody AuthenticationRequest request){
        System.out.println("Prvo");
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }

    @GetMapping("/validate")
    public ResponseEntity<UserTokenInfoDTO> validateToken(@RequestHeader("Authorization") String token) {
        String jwt = token.substring(7);
        if (jwtService.isTokenExpired(jwt)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String username = jwtService.extractUsername(jwt);
        String userType = jwtService.extractUserType(jwt);

        UserTokenInfoDTO dto = new UserTokenInfoDTO(username, userType);
        return ResponseEntity.ok(dto);
    }



    @PostMapping("/refreshToken")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        authenticationService.refreshToken(request, response);
    }

    @PostMapping("/verify-otp")
    public AuthenticationResponse verifyOtp(@RequestBody VerifyOtpRequest req) {
        String username = jwtService.extractUsernameFromPreAuth(req.getPreAuthToken());
        if (!otpService.verifyOtp(username, req.getOtpCode(), "LOGIN_2FA")) {
            throw new BadCredentialsException("Invalid OTP");
        }
        User user = userRepository.findByUsername(username).orElseThrow();
        return authenticationService.completeAuthentication(user);
    }

    @GetMapping("/me/{token}")
    public ResponseEntity<AuthenticationResponse> getCurrentUser(@PathVariable("token") String token) throws Exception {

        String username = jwtService.extractUsername(token);
        if (username == null || jwtService.isTokenExpired(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent()) {

            return ResponseEntity.ok(AuthenticationResponse.builder()
                    .username(user.get().getUsername())
                    .role(user.get().getRole().name())
                    .build());
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

}
