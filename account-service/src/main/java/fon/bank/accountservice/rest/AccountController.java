package fon.bank.accountservice.rest;

import fon.bank.accountservice.dto.AmountRequest;
import fon.bank.accountservice.dto.ClientTransferRequest;
import fon.bank.accountservice.dto.TransferRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import fon.bank.accountservice.dto.AccountDTO;
import fon.bank.accountservice.service.AccountImpl;

import java.util.List;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountImpl accountService;

    @GetMapping("/my")
    @PreAuthorize("hasAuthority('ROLE_CLIENT')")
    public ResponseEntity<List<AccountDTO>> myAccounts() {
        return ResponseEntity.ok(accountService.myAccounts());
    }

    @GetMapping("/byaccountnumber")
    @PreAuthorize("hasAnyAuthority('ROLE_CLIENT','ROLE_EMPLOYEE')")
    public ResponseEntity<AccountDTO> myAccountByAccountNumber(@RequestParam("accountNumber") String accountNumber) {
        return ResponseEntity.ok(accountService.getByAccountNumber(accountNumber));
    }

    @PostMapping("/transfer/my")
    @PreAuthorize("hasAuthority('ROLE_CLIENT')")
    public ResponseEntity<Void> clientTransfer(@RequestBody ClientTransferRequest req) {
        accountService.clientTransfer(req);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/withdraw")
    @PreAuthorize("hasAuthority('ROLE_EMPLOYEE')")
    public ResponseEntity<Void> withdraw(@RequestBody AmountRequest req) {
        accountService.withdraw(req);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/deposit")
    @PreAuthorize("hasAuthority('ROLE_EMPLOYEE')")
    public ResponseEntity<Void> deposit(@RequestBody AmountRequest req) {
        accountService.deposit(req);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/transfer")
    @PreAuthorize("hasAuthority('ROLE_EMPLOYEE')")
    public ResponseEntity<Void> transfer(@RequestBody TransferRequest req) {
        accountService.transfer(req);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/clients/{id}")
    public ResponseEntity<List<AccountDTO>> getAccountsByClient(@PathVariable Long id) {
        List<AccountDTO> accounts = accountService.getAccountsByClientId(id);
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountDTO> getAccountsById(@PathVariable Long id) throws Exception {
        return ResponseEntity.ok(accountService.findById(id));
    }


}
