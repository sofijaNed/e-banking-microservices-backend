package fon.bank.transactionservice.dao;

import fon.bank.transactionservice.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findBySenderAccountId(Long senderAccountId);
    List<Transaction> findByReceiverAccountId(Long receiverAccountId);
    List<Transaction> findBySenderAccountIdInOrReceiverAccountIdIn(
            Collection<Long> senderIds, Collection<Long> receiverIds);
}
