package fon.bank.authservice.security.token;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token,Integer> {
    @Query(value = """
      select t from Token t inner join t.user u
      on t.user.username = u.username
      where u.username = :username and t.expired = false and t.revoked = false
      """)
    List<Token> findAllValidTokenByUser(String username);


    List<Token> findByUserUsername(String username);


    Optional<Token> findByToken(String token);


    void deleteByUserUsername(String username);
}
