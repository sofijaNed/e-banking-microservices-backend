package fon.bank.authservice.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import fon.bank.authservice.entity.Client;
import fon.bank.authservice.entity.User;

@Repository
public interface ClientRepository extends JpaRepository<Client, Integer> {
    Client findClientByUserClient(User user);
    Client findClientByUserClientUsername(String username);
}
