package fon.bank.loanservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Data
public class LoanPaymentId implements Serializable {
    private Long loanId;

    @Column(name = "installment_no", nullable = false)
    private Integer installmentNo;

    public LoanPaymentId() {}
    public LoanPaymentId(Long loanId, Integer installmentNo) {
        this.loanId = loanId;
        this.installmentNo = installmentNo;
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LoanPaymentId)) return false;
        LoanPaymentId that = (LoanPaymentId) o;
        return Objects.equals(loanId, that.loanId) &&
                Objects.equals(installmentNo, that.installmentNo);
    }
    @Override public int hashCode() { return Objects.hash(loanId, installmentNo); }
}