package fon.bank.accountservice.service;

import fon.bank.accountservice.dao.AccountRepository;
import fon.bank.accountservice.dto.AccountDTO;
import fon.bank.accountservice.dto.AmountRequest;
import fon.bank.accountservice.dto.ClientTransferRequest;
import fon.bank.accountservice.dto.TransferRequest;
import fon.bank.accountservice.entity.Account;
import fon.bank.accountservice.exception.AccountNotFoundException;
import fon.bank.accountservice.exception.InsufficientFundsException;
import fon.bank.accountservice.exception.NotOwnerException;
import fon.bank.accountservice.feign.UserClient;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountImpl{

    private final AccountRepository accountRepository;
    private final ModelMapper modelMapper;
    private final UserClient userClient;

    private String currentUsername() {
        var auth = (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        return auth.getToken().getSubject();
    }

    private AccountDTO toDto(Account a) {
        if (a == null) return null;
        return modelMapper.map(a, AccountDTO.class);
    }

    @Transactional(readOnly = true)
    public List<AccountDTO> myAccounts() {
        String username = currentUsername();
        var client = userClient.findClientByUsername(username);
        if (client == null || client.getId() == null) {
            throw new AccountNotFoundException("Client profile not found for " + username);
        }
        return accountRepository.findAccountsByClientId(client.getId())
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public void withdraw(AmountRequest req) {
        validateAmount(req.getAmount());
        int rows = accountRepository.withdrawIfSufficient(req.getAccountNumber(), req.getAmount());
        if (rows == 0) throw new InsufficientFundsException("Insufficient funds or account not found");
    }

    @Transactional
    public void deposit(AmountRequest req) {
        validateAmount(req.getAmount());
        int rows = accountRepository.deposit(req.getAccountNumber(), req.getAmount());
        if (rows == 0) throw new AccountNotFoundException("Account not found");
    }

    // ========== TRANSFERS (LOCK + SAVE) ==========

    @Transactional
    public void transfer(TransferRequest req) {
        basicTransfer(req.getFromAccountNumber(), req.getToAccountNumber(), req.getAmount(), null);
    }

    @Transactional
    public void clientTransfer(ClientTransferRequest req) {
        String username = currentUsername();
        var client = userClient.findClientByUsername(username);
        if (client == null || client.getId() == null) {
            throw new NotOwnerException("Client profile not found");
        }
        basicTransfer(req.getFromAccountNumber(), req.getToAccountNumber(), req.getAmount(), client.getId());
    }

    // ========== helpers ==========

    private void basicTransfer(String fromAccNo, String toAccNo, BigDecimal amount, Long mustOwnClientId) {
        if (fromAccNo == null || toAccNo == null || fromAccNo.isBlank() || toAccNo.isBlank())
            throw new IllegalArgumentException("Both account numbers are required");
        if (fromAccNo.equals(toAccNo))
            throw new IllegalArgumentException("From and To cannot be the same");
        validateAmount(amount);

        final String firstKey  = (fromAccNo.compareTo(toAccNo) <= 0) ? fromAccNo : toAccNo;
        final String secondKey = (fromAccNo.compareTo(toAccNo) <= 0) ? toAccNo   : fromAccNo;

        Account first  = accountRepository.findByAccountNumberForUpdate(firstKey)
                .orElseThrow(() -> new AccountNotFoundException("Account " + firstKey + " not found"));
        Account second = accountRepository.findByAccountNumberForUpdate(secondKey)
                .orElseThrow(() -> new AccountNotFoundException("Account " + secondKey + " not found"));

        Account from = fromAccNo.equals(firstKey) ? first : second;
        Account to   = toAccNo.equals(secondKey) ? second : first;

        if (mustOwnClientId != null && !mustOwnClientId.equals(from.getClientId())) {
            throw new NotOwnerException("From-account does not belong to current client");
        }

        if (from.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException("Insufficient funds");
        }

        from.setBalance(from.getBalance().subtract(amount));
        from.setAvailableBalance(from.getAvailableBalance().subtract(amount));

        to.setBalance(to.getBalance().add(amount));
        to.setAvailableBalance(to.getAvailableBalance().add(amount));
    }

    private static void validateAmount(BigDecimal amount) {
        if (amount == null || amount.signum() <= 0)
            throw new IllegalArgumentException("Amount must be positive");
    }

    public List<AccountDTO> getAccountsByClientId(Long clientId) {
        List<Account> accounts = accountRepository.findAccountsByClientId(clientId);
        if (accounts == null || accounts.isEmpty()) return new ArrayList<>();

        return accounts.stream()
                .map(account -> {
                    return modelMapper.map(account, AccountDTO.class);
                })
                .toList();
    }


}
