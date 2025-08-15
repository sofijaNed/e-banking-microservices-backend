package fon.bank.loanservice.dto;

import lombok.Data;


@Data
public class AccountDTO {
    private Long id;
    private String accountNumber;
    private String currency;
}
