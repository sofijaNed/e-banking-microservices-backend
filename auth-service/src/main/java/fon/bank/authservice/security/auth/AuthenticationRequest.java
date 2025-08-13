package fon.bank.authservice.security.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationRequest {

    @NotBlank(message = "Username is mandatory.")
    private String username;


    @NotBlank(message = "Password is mandatory.")
    private String password;

    private boolean use2fa;

    @Email
    private String email;
}
