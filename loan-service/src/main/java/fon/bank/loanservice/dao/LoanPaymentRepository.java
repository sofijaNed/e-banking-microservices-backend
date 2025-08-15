package fon.bank.loanservice.dao;

import fon.bank.loanservice.entity.LoanPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LoanPaymentRepository extends JpaRepository<LoanPayment, Long> {
    List<LoanPayment> findByLoanId(Long loanId);
}
