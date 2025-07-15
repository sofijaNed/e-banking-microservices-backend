package fon.bank.accountservice.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import fon.bank.accountservice.entity.Account;

import java.util.List;

@Repository
public interface AccountRepository extends JpaRepository<Account, String> {
    List<Account> findAccountsByClient(int id);
}
