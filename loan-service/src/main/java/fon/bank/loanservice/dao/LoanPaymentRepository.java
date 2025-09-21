package fon.bank.loanservice.dao;

import fon.bank.loanservice.entity.LoanPayment;
import fon.bank.loanservice.entity.LoanPaymentId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface LoanPaymentRepository extends JpaRepository<LoanPayment, LoanPaymentId> {

    List<LoanPayment> findByIdLoanIdOrderByIdInstallmentNoAsc(Long loanId);

    List<LoanPayment> findByPaidFalseAndDueDateBefore(LocalDate date);
}
