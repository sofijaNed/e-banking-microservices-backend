package fon.bank.accountservice.dto;

import lombok.Data;

import java.math.BigDecimal;
@Data
public class TransferRequest {
    private String fromAccountNumber;
    private String toAccountNumber;
    private BigDecimal amount;
}
