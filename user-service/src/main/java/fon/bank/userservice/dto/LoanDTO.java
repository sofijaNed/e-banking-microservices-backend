package fon.bank.userservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.LocalDate;

@Data
public class LoanDTO {
    private Integer id;

    private Double principal_amount;

    private Double interest_rate;

    private LocalDate loan_term;

    private LocalDate date_issued;

    private Double monthly_payment;

    private Double outstanding_balance;

}
