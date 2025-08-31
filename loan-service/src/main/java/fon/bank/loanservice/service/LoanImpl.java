package fon.bank.loanservice.service;

import fon.bank.loanservice.dao.LoanPaymentRepository;
import fon.bank.loanservice.dao.LoanRepository;
import fon.bank.loanservice.dto.*;
import fon.bank.loanservice.entity.Loan;
import fon.bank.loanservice.entity.LoanPayment;
import fon.bank.loanservice.entity.LoanStatus;
import fon.bank.loanservice.feign.AccountClient;
import fon.bank.loanservice.feign.TransactionClient;
import fon.bank.loanservice.security.Authorization;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LoanImpl {
    private final LoanRepository loanRepo;
    private final LoanPaymentRepository payRepo;
    private final AccountClient accountClient;
    private final TransactionClient transactionClient;
    private final ModelMapper mapper;
    private final Authorization authorization;

    private static final String BANK_ACCOUNT_NUMBER = "999-0000000001";

    public LoanDTO get(Long id) {
        return loanRepo.findById(id).map(l -> mapper.map(l, LoanDTO.class)).orElse(null);
    }
    public List<LoanDTO> byStatus(String status) {
        var s = LoanStatus.valueOf(status.toUpperCase());
        return loanRepo.findByStatus(s).stream().map(l -> mapper.map(l, LoanDTO.class)).toList();
    }
    public List<LoanDTO> byAccount(Long accountId) {
        if (!authorization.canAccessAccountId(accountId))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        return loanRepo.findByAccountId(accountId).stream().map(l -> mapper.map(l, LoanDTO.class)).toList();
    }
    public List<LoanPaymentDTO> payments(Long loanId) {
        if (!authorization.canAccessLoan(loanId))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        return payRepo.findByLoanId(loanId).stream().map(p -> mapper.map(p, LoanPaymentDTO.class)).toList();
    }


    @Transactional
    public LoanResponseDTO submit(LoanRequestDTO dto) {
        if (!authorization.ownsAccountNumber(dto.getAccountNumber()))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        if (dto.getAmount() == null || dto.getAmount().signum() <= 0) throw new IllegalArgumentException("Invalid amount");
        if (dto.getTermMonths() == null || dto.getTermMonths() <= 0)  throw new IllegalArgumentException("Invalid term");
        if (dto.getAccountNumber() == null || dto.getAccountNumber().isBlank()) throw new IllegalArgumentException("Account number required");

        AccountDTO acc = accountClient.findByNumber(dto.getAccountNumber());
        if (acc == null || acc.getId() == null) throw new IllegalArgumentException("Account not found: " + dto.getAccountNumber());

        Loan loan = new Loan();
        loan.setPrincipalAmount(dto.getAmount());
        loan.setInterestRate(dto.getInterestRate());
        loan.setTermMonths(dto.getTermMonths());
        loan.setCurrency(dto.getCurrency());
        loan.setNote(dto.getPurpose());
        loan.setDateIssued(LocalDate.now());
        loan.setOutstandingBalance(dto.getAmount());
        loan.setAccountId(acc.getId());
        loan.setStatus(LoanStatus.PENDING);

        loan = loanRepo.save(loan);

        LoanResponseDTO resp = new LoanResponseDTO();
        resp.setId(loan.getId());
        resp.setStatus(loan.getStatus().name());
        resp.setCreatedAt(loan.getDateIssued());
        resp.setNote(loan.getNote());
        return resp;
    }


    @Transactional
    public LoanResponseDTO approveAndDisburse(Long loanId, ApproveLoanRequest req) {
        Loan loan = loanRepo.findById(loanId).orElseThrow(() -> new IllegalArgumentException("Loan not found"));
        if (loan.getStatus() != LoanStatus.PENDING) throw new IllegalStateException("Loan not pending");

        loan.setApprovedBy(req.getEmployeeId());
        loan.setApprovedAt(LocalDate.now());
        loan.setNote(req.getNote());
        loan.setStatus(LoanStatus.APPROVED);

        BigDecimal r = loan.getInterestRate().divide(BigDecimal.valueOf(1200), 10, RoundingMode.HALF_UP);
        int n = loan.getTermMonths();
        BigDecimal onePlusRPowerN = r.add(BigDecimal.ONE).pow(n);
        BigDecimal monthly = loan.getPrincipalAmount().multiply(r).multiply(onePlusRPowerN)
                .divide(onePlusRPowerN.subtract(BigDecimal.ONE), 2, RoundingMode.HALF_UP);
        loan.setMonthlyPayment(monthly);
        loanRepo.save(loan);

        BigDecimal remaining = loan.getPrincipalAmount();
        for (int i = 1; i <= n; i++) {
            BigDecimal interest = remaining.multiply(r).setScale(2, RoundingMode.HALF_UP);
            BigDecimal principal = monthly.subtract(interest).setScale(2, RoundingMode.HALF_UP);
            remaining = remaining.subtract(principal).setScale(2, RoundingMode.HALF_UP);

            LoanPayment lp = new LoanPayment();
            lp.setLoan(loan);
            lp.setDueDate(LocalDate.now().plusMonths(i));
            lp.setAmount(monthly);
            lp.setCurrency(loan.getCurrency());
            lp.setPrincipalAmount(principal);
            lp.setInterestAmount(interest);
            payRepo.save(lp);
        }

        AccountDTO clientAcc = accountClient.findById(loan.getAccountId());
        if (clientAcc == null || clientAcc.getAccountNumber() == null)
            throw new IllegalStateException("Client account not found for loan.accountId=" + loan.getAccountId());

        accountClient.transfer(new TransferCommand(BANK_ACCOUNT_NUMBER, clientAcc.getAccountNumber(), loan.getPrincipalAmount()));

        TransactionDTO log = new TransactionDTO();
        log.setSender(BANK_ACCOUNT_NUMBER);
        log.setReceiver(clientAcc.getAccountNumber());
        log.setAmount(loan.getPrincipalAmount());
        log.setCurrency(loan.getCurrency());
        log.setDescription("Loan disbursement for loan #" + loan.getId());
        log.setType("BANK_TO_CLIENT");
        log.setStatus("COMPLETED");
        transactionClient.log(log);

        loan.setStatus(LoanStatus.DISBURSED);
        loanRepo.save(loan);

        LoanResponseDTO resp = new LoanResponseDTO();
        resp.setId(loan.getId());
        resp.setStatus(loan.getStatus().name());
        resp.setCreatedAt(loan.getDateIssued());
        resp.setApprovedAt(loan.getApprovedAt());
        resp.setApprovedByEmployeeId(loan.getApprovedBy());
        resp.setNote(loan.getNote());
        return resp;
    }

    @Transactional
    public void payInstallment(Long paymentId, Long clientAccountId) {
        if (!authorization.canAccessAccountId(clientAccountId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        LoanPayment p = payRepo.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found"));
        if (p.isPaid()) throw new IllegalStateException("Already paid");
        Loan loan = p.getLoan();
        if (loan == null) throw new IllegalStateException("Loan not linked to payment");

        if (!authorization.canAccessAccountId(loan.getAccountId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        AccountDTO acc = accountClient.findById(clientAccountId);
        if (acc == null || acc.getAccountNumber() == null || acc.getAccountNumber().isBlank()) {
            throw new IllegalArgumentException("fromAccountNumber is required");
        }
        String fromAccount = acc.getAccountNumber();
        String toAccount   = BANK_ACCOUNT_NUMBER;

        accountClient.transfer(new TransferCommand(fromAccount, toAccount, p.getAmount()));

        TransactionDTO log = new TransactionDTO();
        log.setSender(fromAccount);
        log.setReceiver(toAccount);
        log.setAmount(p.getAmount());
        log.setCurrency(p.getCurrency() != null ? p.getCurrency() : loan.getCurrency());
        log.setDescription("Loan installment payment for loan #" + loan.getId());
        log.setType("CLIENT_TO_BANK");
        log.setStatus("COMPLETED");
        transactionClient.log(log);

        p.setPaid(true);
        p.setPaidAt(java.time.LocalDate.now());
        payRepo.save(p);
    }

    public List<LoanDTO> findAllByStatus(String status) {
        LoanStatus enumStatus;
        try {
            enumStatus = LoanStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid loan status: " + status);
        }
        return loanRepo.findByStatus(enumStatus).stream()
                .map(loan -> {
                    LoanDTO dto = new LoanDTO();
                    dto.setId(loan.getId());
                    dto.setPrincipalAmount(loan.getPrincipalAmount());
                    dto.setInterestRate(loan.getInterestRate());
                    dto.setTermMonths(loan.getTermMonths());
                    dto.setCurrency(loan.getCurrency());
                    dto.setNote(loan.getNote());
                    dto.setDateIssued(loan.getDateIssued());
                    dto.setMonthlyPayment(loan.getMonthlyPayment());
                    dto.setOutstandingBalance(loan.getOutstandingBalance());
                    dto.setAccount(loan.getAccountId());
                    dto.setApprovedBy(loan.getApprovedBy());
                    dto.setApprovedAt(loan.getApprovedAt());
                    return dto;
                }).toList();
    }


    public List<LoanDTO> findAllByClientId(Long clientId) {
        if (!authorization.isSelf(clientId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return loanRepo.findByAccountId(clientId).stream()
                .map(loan -> {
                    LoanDTO dto = new LoanDTO();
                    dto.setId(loan.getId());
                    dto.setPrincipalAmount(loan.getPrincipalAmount());
                    dto.setInterestRate(loan.getInterestRate());
                    dto.setTermMonths(loan.getTermMonths());
                    dto.setCurrency(loan.getCurrency());
                    dto.setNote(loan.getNote());
                    dto.setDateIssued(loan.getDateIssued());
                    dto.setMonthlyPayment(loan.getMonthlyPayment());
                    dto.setOutstandingBalance(loan.getOutstandingBalance());
                    dto.setAccount(loan.getAccountId());
                    dto.setApprovedBy(loan.getApprovedBy());
                    dto.setApprovedAt(loan.getApprovedAt());
                    return dto;
                })
                .toList();
    }

}
