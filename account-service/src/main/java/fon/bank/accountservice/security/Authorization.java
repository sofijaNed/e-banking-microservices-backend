package fon.bank.accountservice.security;
import fon.bank.accountservice.dao.AccountRepository;
import fon.bank.accountservice.feign.UserClient;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("authorization")
@RequiredArgsConstructor
public class Authorization {
    private final AccountRepository accounts;
    private final UserClient users;

    private boolean isEmployee(Authentication a) {
        return a.getAuthorities().stream().anyMatch(ga -> ga.getAuthority().equals("ROLE_EMPLOYEE"));
    }
    private String username() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
    public Long currentClientId() {
        return users.findClientByUsername(username()).getId();
    }

    public boolean canAccessAccount(Long accountId) {
        var a = SecurityContextHolder.getContext().getAuthentication();
        if (isEmployee(a)) return true;
        return accounts.existsByIdAndClientId(accountId, currentClientId());
    }

    public boolean ownsAccountNumber(String accountNumber) {
        var a = SecurityContextHolder.getContext().getAuthentication();
        if (isEmployee(a)) return true;
        return accounts.existsByAccountNumberAndClientId(accountNumber, currentClientId());
    }

    public boolean isSelfClientId(Long id) {
        var a = SecurityContextHolder.getContext().getAuthentication();
        if (isEmployee(a)) return true;
        Long mine = currentClientId();
        return mine != null && mine.equals(id);
    }
}
