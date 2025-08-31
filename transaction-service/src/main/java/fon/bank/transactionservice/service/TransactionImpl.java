package fon.bank.transactionservice.service;

import fon.bank.transactionservice.dto.AccountDTO;
import fon.bank.transactionservice.dto.TransferCommand;
import fon.bank.transactionservice.entity.TransactionStatus;
import fon.bank.transactionservice.entity.TransactionType;
import fon.bank.transactionservice.feign.AccountClient;
import fon.bank.transactionservice.security.Authorization;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import fon.bank.transactionservice.dao.TransactionRepository;
import fon.bank.transactionservice.dto.TransactionDTO;
import fon.bank.transactionservice.entity.Transaction;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TransactionImpl implements TransactionService{

    private final TransactionRepository repo;
    private final AccountClient accountClient;
    private final ModelMapper mapper;
    private final Authorization authorization;

    public TransactionImpl(TransactionRepository repo, AccountClient accountClient, ModelMapper mapper, Authorization authorization) {
        this.repo = repo;
        this.accountClient = accountClient;
        this.mapper = mapper;
        this.authorization = authorization;
    }

    private String currentUsername() {
        Authentication a = SecurityContextHolder.getContext().getAuthentication();
        if (a instanceof JwtAuthenticationToken jat) return jat.getToken().getSubject();
        return "system";
    }

    private void validate(TransactionDTO req) {
        if (req.getAmount() == null || req.getAmount().compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("Amount must be greater than 0");
        if (req.getSender().equals(req.getReceiver()))
            throw new IllegalArgumentException("Sender and receiver must be different");
    }

    private TransactionDTO toDto(Transaction t) {
        return mapper.map(t, TransactionDTO.class);
    }

    private Long resolveId(String accountNumber) {
        AccountDTO a = accountClient.findByNumber(accountNumber);
        if (a == null || a.getId() == null)
            throw new IllegalArgumentException("Account not found: " + accountNumber);
        return a.getId();
    }

    private void ensureCurrencyMatches(String senderNumber, String currency) {
        AccountDTO a = accountClient.findByNumber(senderNumber);
        if (a == null || a.getCurrency() == null)
            throw new IllegalArgumentException("Cannot resolve currency for " + senderNumber);
        if (!a.getCurrency().equalsIgnoreCase(currency))
            throw new IllegalArgumentException("Currency mismatch: sender=" + a.getCurrency() + ", req=" + currency);
    }

    private Transaction bootstrapPending(TransactionDTO req) {
        Transaction t = new Transaction();
        t.setAmount(req.getAmount());
        t.setCurrency(req.getCurrency());
        t.setDescription(req.getDescription());
        t.setReference(req.getReference());
        t.setModel(req.getModel());
        t.setNumber(req.getNumber());
        t.setType(TransactionType.valueOf(req.getType().trim().toUpperCase()));
        t.setStatus(TransactionStatus.PENDING);
        t.setDate(LocalDateTime.now());
        t.setCreatedBy(currentUsername());

        t.setSenderAccountId(resolveId(req.getSender()));
        t.setReceiverAccountId(resolveId(req.getReceiver()));
        return repo.save(t);
    }

    private void callAccountService(boolean client, TransactionDTO req) {
        TransferCommand cmd = new TransferCommand();
        cmd.setFromAccountNumber(req.getSender());
        cmd.setToAccountNumber(req.getReceiver());
        cmd.setAmount(req.getAmount());
        if (client) accountClient.clientTransfer(cmd);
        else        accountClient.transfer(cmd);
    }


    @Transactional
    public TransactionDTO createClientTransaction(TransactionDTO req) {
        if (!authorization.ownsAccountNumber(req.getSender())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        validate(req);
        ensureCurrencyMatches(req.getSender(), req.getCurrency());
        Transaction t = bootstrapPending(req);

        try {
            callAccountService(true, req);
            t.setStatus(TransactionStatus.COMPLETED);
            t.setUpdatedBy(currentUsername());
            t.setUpdatedAt(Instant.now());
            t = repo.save(t);
            return toDto(t);
        } catch (Exception ex) {
            t.setStatus(TransactionStatus.FAILED);
            String desc = t.getDescription() == null ? "" : (t.getDescription() + " | ");
            t.setDescription(desc + "Error: " + ex.getMessage());
            t.setUpdatedBy(currentUsername());
            t.setUpdatedAt(Instant.now());
            repo.save(t);
            throw ex;
        }
    }

    @Transactional
    public TransactionDTO createInternalTransaction(TransactionDTO req) {
        validate(req);
        ensureCurrencyMatches(req.getSender(), req.getCurrency());
        Transaction t = bootstrapPending(req);

        try {
            callAccountService(false, req);
            t.setStatus(TransactionStatus.COMPLETED);
            t.setUpdatedBy(currentUsername());
            t.setUpdatedAt(Instant.now());
            t = repo.save(t);
            return toDto(t);
        } catch (Exception ex) {
            t.setStatus(TransactionStatus.FAILED);
            String desc = t.getDescription() == null ? "" : (t.getDescription() + " | ");
            t.setDescription(desc + "Error: " + ex.getMessage());
            t.setUpdatedBy(currentUsername());
            t.setUpdatedAt(Instant.now());
            repo.save(t);
            throw ex;
        }
    }

    @Override
    public List<TransactionDTO> findBySenderAccountNumber(String senderNumber) {
        if (!authorization.canAccessAccountNumber(senderNumber)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        Long id = resolveId(senderNumber);
        return repo.findBySenderAccountId(id).stream().map(this::toDto).toList();
    }

    @Override
    public List<TransactionDTO> findByReceiverAccountNumber(String receiverNumber) {
        Long id = resolveId(receiverNumber);
        return repo.findByReceiverAccountId(id).stream().map(this::toDto).toList();
    }

    @Override
    public TransactionDTO findById(Long id) throws Exception {
        return repo.findById(id).map(this::toDto).orElse(null);
    }

    @Override
    public List<TransactionDTO> findAll() {
        return repo.findAll().stream().map(this::toDto).toList();
    }

    @Transactional
    public void log(TransactionDTO req) {
        if (req == null) throw new IllegalArgumentException("Body is required");
        if (req.getAmount() == null || req.getAmount().signum() <= 0)
            throw new IllegalArgumentException("Amount must be > 0");
        if (req.getSender() == null || req.getReceiver() == null)
            throw new IllegalArgumentException("Sender and receiver account numbers are required");
        if (req.getSender().equals(req.getReceiver()))
            throw new IllegalArgumentException("Sender and receiver must be different");

        Transaction t = new Transaction();
        t.setSenderAccountId(resolveId(req.getSender()));
        t.setReceiverAccountId(resolveId(req.getReceiver()));

        t.setAmount(req.getAmount());
        t.setCurrency(req.getCurrency());
        t.setDescription(req.getDescription());
        t.setReference(req.getReference());
        t.setModel(req.getModel());
        t.setNumber(req.getNumber());
        t.setDate(java.time.LocalDateTime.now());

        if (req.getType() != null)
            t.setType(TransactionType.valueOf(req.getType().trim().toUpperCase()));
        if (req.getStatus() != null)
            t.setStatus(TransactionStatus.valueOf(req.getStatus().trim().toUpperCase()));
        else
            t.setStatus(TransactionStatus.PENDING);

        t.setCreatedBy(currentUsername());

        repo.save(t);
    }

}
