package fon.bank.accountservice.entity;

import jakarta.persistence.*;
import lombok.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "account")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "account_number", unique = true, nullable = false)
    private String accountNumber;

    @Enumerated(EnumType.STRING)
    @Column(name="type")
    private AccountType type;

    @Column(name = "iban")
    private String iban;

    @Column(name = "balance", precision = 19, scale = 4, nullable = false)
    private BigDecimal balance = BigDecimal.ZERO;

    @Column(name = "available_balance", precision = 19, scale = 4, nullable = false)
    private BigDecimal availableBalance = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "currency", nullable = false)
    private Currency currency;

    @Column(name = "opened")
    private LocalDate opened;

    @Version
    private Long version;

    @Column(name="client_id")
    private Long clientId;


    @Column(name="created_at")
    private LocalDateTime createdAt;

    @Column(name="created_by")
    private String createdBy;

    @Column(name="updated_at")
    private LocalDateTime updatedAt;

    @Column(name="updated_by")
    private String updatedBy;

}
