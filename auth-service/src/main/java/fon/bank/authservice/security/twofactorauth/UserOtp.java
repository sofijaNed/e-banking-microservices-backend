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

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "username", referencedColumnName = "username")
    @JsonIgnore
    private User user;

    private String otpHash;
    private LocalDateTime expiresAt;
    private LocalDateTime createdAt;
    private boolean used;
    private int attempts;
    private String purpose;
}
