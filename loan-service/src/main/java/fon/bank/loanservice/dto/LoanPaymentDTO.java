package fon.bank.loanservice.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class LoanPaymentDTO {
    private Integer installmentNo;
    private Long loanId;
    private LocalDate dueDate;
    private BigDecimal amount;
    private String currency;
    private boolean paid;
    private LocalDate paidAt;
    private BigDecimal principalAmount;
    private BigDecimal interestAmount;
    private String note;
}
