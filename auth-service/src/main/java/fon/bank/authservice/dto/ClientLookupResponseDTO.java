package fon.bank.authservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientLookupResponseDTO {
    private Long id;
    private String email;
    private String username;
}
