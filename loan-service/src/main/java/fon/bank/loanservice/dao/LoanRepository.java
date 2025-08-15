package fon.bank.loanservice.dao;

import fon.bank.loanservice.entity.Loan;
import fon.bank.loanservice.entity.LoanStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LoanRepository extends JpaRepository<Loan, Long> {
    List<Loan> findByStatus(LoanStatus status);
    List<Loan> findByAccountId(Long accountId);
}