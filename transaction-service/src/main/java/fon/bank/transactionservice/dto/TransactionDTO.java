package fon.bank.transactionservice.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TransactionDTO {
    private Long id;

    @Size(max = 50, message = "Sender cannot exceed 50 characters")
    private String sender;

    @Size(max = 50, message = "Sender cannot exceed 50 characters")
    private String receiver;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", inclusive = true, message = "Amount must be greater than 0")
    private BigDecimal amount;

    @NotBlank(message = "Currency is required")
    @Pattern(regexp = "^[A-Z]{3}$", message = "Currency must be a 3-letter ISO code (e.g., EUR, USD)")
    private String currency;

    @Size(max = 10, message = "Model cannot exceed 10 characters")
    private String model;

    @Size(max = 20, message = "Number cannot exceed 20 characters")
    private String number;

    private LocalDateTime date;

    @Size(max = 255, message = "Description cannot exceed 255 characters")
    private String description;

    @Pattern(regexp = "PENDING|COMPLETED|FAILED", message = "Status must be PENDING, COMPLETED, or FAILED")
    private String status;

    @Size(max = 30, message = "Reference cannot exceed 30 characters")
    private String reference;

    @Pattern(regexp = "CLIENT_TO_CLIENT|CLIENT_TO_BANK|BANK_TO_CLIENT",
            message = "Type must be CLIENT_TO_CLIENT,CLIENT_TO_BANK or BANK_TO_CLIENT")
    private String type;

}
