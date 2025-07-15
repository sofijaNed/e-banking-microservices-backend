package fon.bank.userservice.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import fon.bank.userservice.entity.Client;

@Repository
public interface ClientRepository extends JpaRepository<Client, Integer> {
//    Client findClientByUserClientUsername(String username);
      Client findByUserClient(String username);
}
