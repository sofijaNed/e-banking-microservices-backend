package fon.bank.authservice.security.twofactorauth;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fon.bank.authservice.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_otp")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserOtp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "username", referencedColumnName = "username", nullable = true)
    @JsonIgnore
    private User user;

    @Column(name = "email", length = 150)
    private String email;

    @Column(name = "client_id")
    private Long clientId;

    private String otpHash;
    private LocalDateTime expiresAt;
    private LocalDateTime createdAt;
    private boolean used;
    private int attempts;
    private String purpose;

    @Column(name="ticket_id", length=64)
    private String ticketId;

    @Column(name="reserved_username", length=50)
    private String reservedUsername;

    @Column(name="password_hash", length=255)
    private String passwordHash;
}
