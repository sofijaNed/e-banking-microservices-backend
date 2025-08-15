package fon.bank.loanservice.rest;

import fon.bank.loanservice.dto.*;
import fon.bank.loanservice.service.LoanImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/loans")
@RequiredArgsConstructor
public class LoanController {
    private final LoanImpl service;

    @PostMapping("/submit")
    @PreAuthorize("hasAuthority('ROLE_CLIENT')")
    public ResponseEntity<LoanResponseDTO> submit(@RequestBody LoanRequestDTO req) {
        return ResponseEntity.ok(service.submit(req));
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasAuthority('ROLE_EMPLOYEE')")
    public ResponseEntity<LoanResponseDTO> approve(@PathVariable Long id, @RequestBody ApproveLoanRequest req) {
        return ResponseEntity.ok(service.approveAndDisburse(id, req));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_CLIENT','ROLE_EMPLOYEE')")
    public ResponseEntity<LoanDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(service.get(id));
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasAuthority('ROLE_EMPLOYEE')")
    public ResponseEntity<List<LoanDTO>> byStatus(@PathVariable String status) {
        return ResponseEntity.ok(service.byStatus(status));
    }

    @GetMapping("/account/{accountId}")
    @PreAuthorize("hasAnyAuthority('ROLE_CLIENT','ROLE_EMPLOYEE')")
    public ResponseEntity<List<LoanDTO>> byAccount(@PathVariable Long accountId) {
        return ResponseEntity.ok(service.byAccount(accountId));
    }

    @GetMapping("/{loanId}/payments")
    @PreAuthorize("hasAnyAuthority('ROLE_CLIENT','ROLE_EMPLOYEE')")
    public ResponseEntity<List<LoanPaymentDTO>> payments(@PathVariable Long loanId) {
        return ResponseEntity.ok(service.payments(loanId));
    }

    @PostMapping("/payments/{paymentId}/pay")
    @PreAuthorize("hasAuthority('ROLE_CLIENT')")
    public ResponseEntity<Void> pay(@PathVariable Long paymentId,
                                    @RequestParam Long clientAccountId) {
        service.payInstallment(paymentId, clientAccountId);
        return ResponseEntity.accepted().build();
    }

//    @GetMapping("/status/{status}")
//    public ResponseEntity<List<LoanDTO>> byLoanStatus(@PathVariable String status) {
//        return ResponseEntity.ok(service.findAllByStatus(status));
//    }

    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<LoanDTO>> byClient(@PathVariable Long clientId) {
        return ResponseEntity.ok(service.findAllByClientId(clientId));
    }
}
