package fon.bank.authservice.registration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationTicketDTO {
    private String ticketId;
    private String emailMasked;
}
