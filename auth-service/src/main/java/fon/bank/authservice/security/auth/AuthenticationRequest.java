package fon.bank.authservice.security.auth;

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

    @NotBlank(message = "Lozinka je obavezna")
    private String username;


    @NotBlank(message = "Lozinka je obavezna")
    private String password;
}
