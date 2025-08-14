package fon.bank.accountservice.dto;

import lombok.Data;

import java.math.BigDecimal;
@Data
public class AmountRequest {
    private String accountNumber;
    private BigDecimal amount;
}
