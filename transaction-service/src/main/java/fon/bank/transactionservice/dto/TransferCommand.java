package fon.bank.transactionservice.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransferCommand {
    private String fromAccountNumber;
    private String toAccountNumber;
    private BigDecimal amount;
}
