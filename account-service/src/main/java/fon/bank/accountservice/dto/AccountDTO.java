package fon.bank.accountservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.*;
import lombok.Data;
import fon.bank.accountservice.entity.AccountType;
import fon.bank.accountservice.entity.Currency;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collection;

@Data
public class AccountDTO {
    private Long id;

    @NotBlank(message = "Account number is required")
    @Size(min = 5, max = 30, message = "Account number must be between 5 and 30 characters")
    private String accountNumber;

    private String type;

    @Pattern(regexp = "[A-Z]{2}\\d{2}[A-Z0-9]{1,30}", message = "Invalid IBAN format")
    private String iban;

    @NotNull(message = "Balance is required")
    @DecimalMin(value = "0.00", inclusive = true, message = "Balance cannot be negative")
    private BigDecimal balance;

    @NotNull(message = "Available balance is required")
    @DecimalMin(value = "0.00", inclusive = true, message = "Available balance cannot be negative")
    private BigDecimal availableBalance;

    @NotBlank(message = "Currency is required")
    @Pattern(regexp = "^[A-Z]{3}$", message = "Currency must be a 3-letter ISO code (e.g., EUR, USD)")
    private String currency;

    @NotNull(message = "Account opening date is required")
    @PastOrPresent(message = "Opening date cannot be in the future")
    private LocalDate opened;
    private Long client;
}
