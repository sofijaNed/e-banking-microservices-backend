package fon.bank.transactionservice.security;
import fon.bank.transactionservice.dto.AccountDTO;
import fon.bank.transactionservice.feign.AccountClient;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("authorization")
@RequiredArgsConstructor
public class Authorization {
    private final AccountClient accounts;

    private boolean isEmployee(Authentication a) {
        return a.getAuthorities().stream().anyMatch(ga -> ga.getAuthority().equals("ROLE_EMPLOYEE"));
    }
    private String username() { return SecurityContextHolder.getContext().getAuthentication().getName(); }

    public boolean ownsAccountNumber(String accountNumber) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (isEmployee(auth)) return true;
        return accounts.myAccounts().stream()
                .map(AccountDTO::getAccountNumber)
                .anyMatch(accNo -> accNo.equals(accountNumber));
    }

    public boolean canAccessAccountNumber(String accountNumber) {
        return ownsAccountNumber(accountNumber);
    }
}
