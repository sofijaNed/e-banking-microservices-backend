package fon.bank.loanservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "loan_payment")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class LoanPayment implements Serializable {

    @EmbeddedId
    @EqualsAndHashCode.Include
    private LoanPaymentId id;

    @MapsId("loanId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "loan_id", nullable = false)
    private Loan loan;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "amount", precision = 19, scale = 4, nullable = false)
    private BigDecimal amount;

    @Column(name = "currency", length = 10)
    private String currency;

    @Column(name = "paid", nullable = false)
    private boolean paid = false;

    @Column(name = "paid_at")
    private LocalDate paidAt;

    @Column(name = "principal_amount", precision = 19, scale = 4)
    private BigDecimal principalAmount;

    @Column(name = "interest_amount", precision = 19, scale = 4)
    private BigDecimal interestAmount;

    @Column(name = "note")
    private String note;
}