package fon.bank.userservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import fon.bank.userservice.entity.AccountType;
import fon.bank.userservice.entity.Currency;

import java.time.LocalDate;
import java.util.Collection;

@Data
public class AccountDTO {
    private String id;

    private AccountType type;

    private Double balance;

    private Currency currency;

    private LocalDate opened;

}
