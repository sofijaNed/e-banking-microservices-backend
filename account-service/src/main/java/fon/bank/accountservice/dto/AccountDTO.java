package fon.bank.accountservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import fon.bank.accountservice.entity.AccountType;
import fon.bank.accountservice.entity.Currency;

import java.time.LocalDate;
import java.util.Collection;

@Data
public class AccountDTO {
    private String id;

    private AccountType type;

    private Double balance;

    private Currency currency;

    private LocalDate opened;

    private Integer client;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Collection<TransactionDTO> sentTransactions;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Collection<TransactionDTO> receivedTransactions;
}
