package fon.bank.userservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.LocalDate;
import java.util.Collection;

@Data
public class ClientDTO {
    private Integer id;

    private String firstname;

    private String lastname;

    private LocalDate birthdate;

    private String email;

    private String phone;

    private String address;

    private String userClient;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Collection<AccountDTO> accountDTOS;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Collection<LoanDTO> loanDTOS;
}
