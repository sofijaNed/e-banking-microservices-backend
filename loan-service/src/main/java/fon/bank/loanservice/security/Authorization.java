package fon.bank.loanservice.security;

import fon.bank.loanservice.dao.LoanRepository;
import fon.bank.loanservice.dto.AccountDTO;
import fon.bank.loanservice.entity.Loan;
import fon.bank.loanservice.feign.AccountClient;
import fon.bank.loanservice.feign.UserClient;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("authorization")
@RequiredArgsConstructor
public class Authorization {
    private final AccountClient accounts;
    private final LoanRepository loans;
    private final UserClient users;

    private boolean isEmployee(Authentication a) {
        return a != null && a.getAuthorities().stream().anyMatch(ga -> ga.getAuthority().equals("ROLE_EMPLOYEE"));
    }
    private Authentication auth() { return SecurityContextHolder.getContext().getAuthentication(); }

    public boolean ownsAccountNumber(String accountNumber) {
        if (isEmployee(auth())) return true;
        List<AccountDTO> mine = accounts.myAccounts();
        return mine.stream().anyMatch(a -> accountNumber.equals(a.getAccountNumber()));
    }

    public boolean canAccessAccountId(Long accountId) {
        if (isEmployee(auth())) return true;
        return accounts.myAccounts().stream().anyMatch(a -> a.getId().equals(accountId));
    }

    public boolean canAccessLoan(Long loanId) {
        if (isEmployee(auth())) return true;
        Loan loan = loans.findById(loanId).orElse(null);
        if (loan == null) return false;
        return canAccessAccountId(loan.getAccountId());
    }

    public boolean isSelf(Long clientId) {
        if (isEmployee(auth())) return true;
        Long myId = users.findClientByUsername(auth().getName()).getId();
        return myId.equals(clientId);

    }
}