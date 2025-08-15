package fon.bank.loanservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransferCommand {
    private String fromAccountNumber;
    private String toAccountNumber;
    private BigDecimal amount;
}
