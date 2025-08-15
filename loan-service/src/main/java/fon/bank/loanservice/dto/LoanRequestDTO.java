package fon.bank.loanservice.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class LoanRequestDTO {
    private String accountNumber;
    private BigDecimal amount;
    private Integer termMonths;
    private BigDecimal interestRate;
    private String currency;
    private String purpose;
}
