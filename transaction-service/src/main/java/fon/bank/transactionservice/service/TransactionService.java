package fon.bank.transactionservice.service;

import fon.bank.transactionservice.dto.TransactionDTO;

import java.util.List;

public interface TransactionService {
    TransactionDTO createClientTransaction(TransactionDTO req);
    TransactionDTO createInternalTransaction(TransactionDTO req);
    List<TransactionDTO> findBySenderAccountNumber(String senderNumber);
    List<TransactionDTO> findByReceiverAccountNumber(String receiverNumber);
    TransactionDTO findById(Long id) throws Exception;
    List<TransactionDTO> findAll();
}
