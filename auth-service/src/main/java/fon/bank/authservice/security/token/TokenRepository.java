package fon.bank.authservice.security.token;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token,Integer> {
    @Query(value = """
      select t from Token t inner join User u\s
      on t.user.username = u.username\s
      where u.username = :username and (t.expired = false or t.revoked = false)\s
      """)
    List<Token> findAllValidTokenByUser(String username);


    List<Token> findByUserUsername(String username);


    Optional<Token> findByToken(String token);


    void deleteByUserUsername(String username);
}
