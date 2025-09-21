package fon.bank.authservice.security.twofactorauth;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserOtpRepository extends JpaRepository<UserOtp, Long> {
    Optional<UserOtp> findTopByUserUsernameAndPurposeOrderByCreatedAtDesc(String username, String purpose);
    Optional<UserOtp> findFirstByPurposeAndTicketIdAndUsedFalseAndExpiresAtAfter(
            String purpose, String ticketId, LocalDateTime now);
    List<UserOtp> findByUserUsernameAndUsedFalseAndExpiresAtAfter(String username, LocalDateTime now);
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query(value = "UPDATE user_otp SET used = 1, username = :username WHERE id = :id", nativeQuery = true)
    int markUsedAndLink(@Param("id") Long id, @Param("username") String username);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query(value = "UPDATE user_otp SET used = 1, username = :username WHERE ticket_id = :ticketId AND used = 0", nativeQuery = true)
    int markUsedAndLinkByTicket(@Param("ticketId") String ticketId, @Param("username") String username);
}
