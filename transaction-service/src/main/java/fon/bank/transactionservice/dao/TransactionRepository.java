package fon.bank.transactionservice.dao;

import fon.bank.transactionservice.entity.Transaction;
import fon.bank.transactionservice.entity.TransactionPK;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, TransactionPK> {

    List<Transaction> findTransactionsByTransactionPK_Sender(String accountid);
    List<Transaction> findTransactionsByTransactionPK_Receiver(String accountid);
}
