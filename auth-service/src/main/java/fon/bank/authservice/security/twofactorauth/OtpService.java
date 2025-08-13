package fon.bank.authservice.security.twofactorauth;

import fon.bank.authservice.entity.User;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OtpService {
    private final UserOtpRepository otpRepo;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    public void generateAndSendOtp(User username, String email, String purpose) {
        String otp = RandomStringUtils.randomNumeric(6);
        String hash = passwordEncoder.encode(otp);
        UserOtp entry = new UserOtp();
        entry.setUser(username);
        entry.setOtpHash(hash);
        entry.setExpiresAt(LocalDateTime.now().plusMinutes(5));
        entry.setCreatedAt(LocalDateTime.now());
        entry.setUsed(false);
        entry.setAttempts(0);
        entry.setPurpose(purpose);
        otpRepo.save(entry);

        String text = String.format("Vaš verifikacioni kod je: %s\nVaži 5 minuta.", otp);
        emailService.sendOtpEmail(email, "Vaš OTP kod", text);
    }

    public boolean verifyOtp(String username, String otp, String purpose) {
        var opt = otpRepo.findTopByUserUsernameAndPurposeOrderByCreatedAtDesc(username, purpose);
        if (opt.isEmpty()) return false;
        UserOtp entry = opt.get();
        if (entry.isUsed()) return false;
        if (entry.getExpiresAt().isBefore(LocalDateTime.now())) return false;
        if (entry.getAttempts() >= 5) return false;
        boolean matches = passwordEncoder.matches(otp, entry.getOtpHash());
        if (!matches) {
            entry.setAttempts(entry.getAttempts() + 1);
            otpRepo.save(entry);
            return false;
        }
        entry.setUsed(true);
        otpRepo.save(entry);
        return true;
    }
}
