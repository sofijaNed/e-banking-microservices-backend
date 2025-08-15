package fon.bank.loanservice.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class LoanDTO {
    private Long id;

    @NotNull(message = "Principal amount is required")
    @DecimalMin(value = "0.01", inclusive = true, message = "Principal amount must be greater than 0")
    private BigDecimal principalAmount;

    @NotNull(message = "Interest rate is required")
    @DecimalMin(value = "0.01", inclusive = true, message = "Interest rate must be greater than 0")
    @DecimalMax(value = "100.00", inclusive = true, message = "Interest rate must be less than or equal to 100")
    private BigDecimal interestRate;

    @NotNull(message = "Term in months is required")
    @Min(value = 1, message = "Term must be at least 1 month")
    @Max(value = 360, message = "Term cannot exceed 360 months (30 years)")
    private Integer termMonths;

    @NotBlank(message = "Currency is required")
    @Pattern(regexp = "^[A-Z]{3}$", message = "Currency must be a 3-letter ISO code (e.g., EUR, USD)")
    private String currency;

    @Size(max = 500, message = "Note cannot exceed 500 characters")
    private String note;

    @NotNull(message = "Date issued is required")
    @PastOrPresent(message = "Date issued cannot be in the future")
    private LocalDate dateIssued;

    @NotNull(message = "Monthly payment is required")
    @DecimalMin(value = "0.01", inclusive = true, message = "Monthly payment must be greater than 0")
    private BigDecimal monthlyPayment;

    @NotNull(message = "Outstanding balance is required")
    @DecimalMin(value = "0.00", inclusive = true, message = "Outstanding balance cannot be negative")
    private BigDecimal outstandingBalance;

    @NotNull(message = "Account ID is required")
    private Long account;

    @NotNull(message = "Approved by (employee ID) is required")
    private Long approvedBy;

    @PastOrPresent(message = "Approval date cannot be in the future")
    private LocalDate approvedAt;
}
