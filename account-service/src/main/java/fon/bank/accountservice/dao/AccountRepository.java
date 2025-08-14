package fon.bank.accountservice.dao;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import fon.bank.accountservice.entity.Account;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    List<Account> findAccountsByClientId(Long clientId);

    Optional<Account> findByAccountNumber(String accountNumber);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from Account a where a.accountNumber = :accountNumber")
    Optional<Account> findByAccountNumberForUpdate(@Param("accountNumber") String accountNumber);

    @Modifying
    @Query("""
    update Account a
       set a.balance = a.balance - :amount,
           a.availableBalance = a.availableBalance - :amount
     where a.accountNumber = :accountNumber
       and a.balance >= :amount
  """)
    int withdrawIfSufficient(@Param("accountNumber") String accountNumber,
                             @Param("amount") BigDecimal amount);

    @Modifying
    @Query("""
    update Account a
       set a.balance = a.balance + :amount,
           a.availableBalance = a.availableBalance + :amount
     where a.accountNumber = :accountNumber
  """)
    int deposit(@Param("accountNumber") String accountNumber,
                @Param("amount") BigDecimal amount);

}

