package fon.bank.userservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientLookupRequestDTO {
    @NotBlank
    private String jmbg;
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
    private String idCardNo;
}
