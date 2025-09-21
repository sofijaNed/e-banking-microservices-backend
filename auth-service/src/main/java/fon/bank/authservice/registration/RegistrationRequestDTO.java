package fon.bank.authservice.registration;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationRequestDTO {
    @NotBlank
    private String username;
    @NotBlank @Size(min=10,max=100) private String password;
    @NotBlank @Size(min=10,max=100) private String confirmPassword;
    @NotBlank @Size(min=13,max=13) private String jmbg;
    private String idCardNo;
    @NotBlank private String firstName;
    @NotBlank private String lastName;
    private String email;
}
