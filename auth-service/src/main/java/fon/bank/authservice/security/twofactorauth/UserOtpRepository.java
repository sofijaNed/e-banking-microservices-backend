package fon.bank.authservice.security.twofactorauth;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserOtpRepository extends JpaRepository<UserOtp, Long> {
    Optional<UserOtp> findTopByUserUsernameAndPurposeOrderByCreatedAtDesc(String username, String purpose);
    List<UserOtp> findByUserUsernameAndUsedFalseAndExpiresAtAfter(String username, LocalDateTime now);
}
