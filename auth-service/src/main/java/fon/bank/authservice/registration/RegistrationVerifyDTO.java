package fon.bank.authservice.registration;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationVerifyDTO {
    @NotBlank
    private String ticketId;
    @NotBlank
    private String otpCode;
}
