package fon.bank.userservice.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import fon.bank.userservice.entity.Client;

import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
      Client findByUserClient(String username);
    Optional<Client> findByJmbg(String jmbg);
    Optional<Client> findByEmail(String email);
    boolean existsByJmbg(String jmbg);
    boolean existsByIdAndUserClientIsNull(Long id);
}
