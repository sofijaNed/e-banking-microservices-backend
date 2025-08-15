package fon.bank.loanservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="loan")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class Loan implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @Column(name = "principal_amount", precision=19, scale=4)
    private BigDecimal principalAmount;

    @Column(name = "interest_rate", precision=19, scale=4)
    private BigDecimal interestRate;

    @Column(name = "term_months")
    private Integer termMonths;

    @Column(name = "currency")
    private String currency;

    @Column(name = "note")
    private String note;

    @Column(name = "date_issued")
    private LocalDate dateIssued;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private LoanStatus status;

    @Column(name = "monthly_payment", precision=19, scale=4)
    private BigDecimal monthlyPayment;

    @Column(name = "outstanding_balance", precision=19, scale=4)
    private BigDecimal outstandingBalance;

    @Column(name="approved_by")
    private Long approvedBy;

    @Column(name="approved_at")
    private LocalDate approvedAt;


    @Column(name="created_at")
    private LocalDateTime createdAt;

    @Column(name="updated_at")
    private LocalDateTime updatedAt;

    @Column(name="created_by")
    private String createdBy;

    @Column(name="updated_by")
    private String updatedBy;

    @Column(name="account_id")
    private Long accountId;


    @OneToMany(mappedBy = "loan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LoanPayment> payments = new ArrayList<>();
}
