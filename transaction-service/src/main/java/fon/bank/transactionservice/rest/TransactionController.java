package fon.bank.transactionservice.rest;

import fon.bank.transactionservice.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import fon.bank.transactionservice.dto.TransactionDTO;
import fon.bank.transactionservice.service.TransactionImpl;

import java.util.List;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionService service;
    public TransactionController(TransactionService service) { this.service = service; }

    @PreAuthorize("hasAuthority('ROLE_EMPLOYEE') or (hasAuthority('ROLE_CLIENT') and @authorization.ownsAccountNumber(#req.sender))")
    @PostMapping("/client")
    public ResponseEntity<TransactionDTO> client(@Valid @RequestBody TransactionDTO req) {
        return ResponseEntity.ok(service.createClientTransaction(req));
    }

    @PreAuthorize("hasAuthority('ROLE_EMPLOYEE')")
    @PostMapping("/internal")
    public ResponseEntity<TransactionDTO> internal(@Valid @RequestBody TransactionDTO req) {
        return ResponseEntity.ok(service.createInternalTransaction(req));
    }

    @PreAuthorize("hasAuthority('ROLE_EMPLOYEE') or @authorization.canAccessAccountNumber(#accountNumber)")
    @GetMapping("/by-sender/{accountNumber}")
    public List<TransactionDTO> bySender(@PathVariable String accountNumber) {
        return service.findBySenderAccountNumber(accountNumber);
    }

    @PreAuthorize("hasAuthority('ROLE_EMPLOYEE') or @authorization.canAccessAccountNumber(#accountNumber)")
    @GetMapping("/by-receiver/{accountNumber}")
    public List<TransactionDTO> byReceiver(@PathVariable String accountNumber) {
        return service.findByReceiverAccountNumber(accountNumber);
    }

    @PreAuthorize("hasAuthority('ROLE_EMPLOYEE')")
    @GetMapping("/{id}")
    public ResponseEntity<TransactionDTO> get(@PathVariable Long id) throws Exception {
        TransactionDTO dto = service.findById(id);
        return dto == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(dto);
    }

    @PostMapping(value = "/_log", consumes = "application/json")
    @PreAuthorize("hasAnyAuthority('ROLE_CLIENT','ROLE_EMPLOYEE')")
    public ResponseEntity<Void> log(@Valid @RequestBody TransactionDTO req) {
        service.log(req);
        return ResponseEntity.accepted().build();
    }

    @PreAuthorize("hasAuthority('ROLE_EMPLOYEE')")
    @GetMapping
    public List<TransactionDTO> all() { return service.findAll(); }
}
