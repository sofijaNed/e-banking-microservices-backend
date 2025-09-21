package fon.bank.authservice.registration;

import feign.FeignException;
import fon.bank.authservice.dao.UserRepository;
import fon.bank.authservice.dto.ClientLookupRequestDTO;
import fon.bank.authservice.dto.ClientLookupResponseDTO;
import fon.bank.authservice.dto.LinkUserRequestDTO;
import fon.bank.authservice.entity.Role;
import fon.bank.authservice.entity.User;
import fon.bank.authservice.feign.ClientServiceClient;
import fon.bank.authservice.security.twofactorauth.EmailService;
import fon.bank.authservice.security.twofactorauth.UserOtp;
import fon.bank.authservice.security.twofactorauth.UserOtpRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RegistrationService {

    private final ClientServiceClient clientService;
    private final UserRepository userRepo;
    private final UserOtpRepository otpRepo;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public RegistrationTicketDTO request(RegistrationRequestDTO dto) {
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            throw bad("Lozinke se ne poklapaju");
        }
        if (userRepo.existsByUsername(dto.getUsername())) {
            throw conflict("Korisničko ime je zauzeto");
        }
        if (!Jmbg.isValid(dto.getJmbg())) {
            throw bad("Neispravan JMBG");
        }

        ClientLookupResponseDTO client;
        try {
            client = clientService.lookup(new ClientLookupRequestDTO(
                    dto.getJmbg(), dto.getFirstName(), dto.getLastName(), dto.getIdCardNo()
            ));
        } catch (FeignException.NotFound e) {
            throw notFound("Klijent sa datim JMBG ne postoji");
        } catch (FeignException e) {
            throw bad("Greška pri proveri klijenta");
        }

        if (client.getUsername() != null && !client.getUsername().isBlank()) {
            throw conflict("Klijent je već registrovan");
        }

        String email = client.getEmail();
        if (email == null || email.isBlank()) {
            // fallback: dozvoli email iz zahteva ako ga šalješ sa fronta
            if (dto.getEmail() == null || dto.getEmail().isBlank()) {
                throw bad("Nije pronađen email za klijenta");
            }
            email = dto.getEmail();
        }
        email = email.toLowerCase(Locale.ROOT);

        String ticketId = UUID.randomUUID().toString();
        String rawOtp = RandomStringUtils.randomNumeric(6);
        String otpHash = passwordEncoder.encode(rawOtp);

        UserOtp otp = new UserOtp();
        otp.setUser(null);
        otp.setEmail(email);
        otp.setClientId(client.getId());
        otp.setTicketId(ticketId);
        otp.setReservedUsername(dto.getUsername());
        otp.setPasswordHash(passwordEncoder.encode(dto.getPassword())); // bcrypt unapred
        otp.setOtpHash(otpHash);
        otp.setPurpose("REGISTRATION");
        otp.setCreatedAt(LocalDateTime.now());
        otp.setExpiresAt(LocalDateTime.now().plusMinutes(10));
        otp.setUsed(false);
        otp.setAttempts(0);
        otpRepo.save(otp);

//        emailService.sendOtpEmail(email,
//                "Registracija - verifikacioni kod",
//                String.format("Vaš verifikacioni kod je: %s%nVaži 10 minuta.", rawOtp));

        emailService.sendOtp(email, rawOtp);

        return new RegistrationTicketDTO(ticketId, maskEmail(email));
    }

    @Transactional
    public void verify(RegistrationVerifyDTO dto) {
        var now = LocalDateTime.now();
        UserOtp otp = otpRepo.findFirstByPurposeAndTicketIdAndUsedFalseAndExpiresAtAfter(
                "REGISTRATION", dto.getTicketId(), now
        ).orElseThrow(() -> bad("Neispravan ili istekao kod (ticket)"));

        if (otp.getAttempts() >= 5) throw bad("Previše pokušaja");

        boolean ok = passwordEncoder.matches(dto.getOtpCode(), otp.getOtpHash());
        if (!ok) {
            otp.setAttempts(otp.getAttempts() + 1);
            otpRepo.save(otp);
            throw bad("Pogrešan OTP");
        }

        if (userRepo.existsByUsername(otp.getReservedUsername())) {
            throw conflict("Korisničko ime je zauzeto");
        }

        // 1) Kreiraj korisnika u authdb
        User u = new User();
        u.setUsername(otp.getReservedUsername());
        u.setPassword(otp.getPasswordHash());  // već bcrypt
        u.setRole(Role.ROLE_CLIENT);
        u.setTwoFactorEnabled(false);
        u.setTwoFactorMethod("EMAIL");
        log.info("VERIFY: pre save user={}", otp.getReservedUsername());
        userRepo.save(u);
        log.info("VERIFY: posle save user={}", u.getUsername());

        log.info("VERIFY: pre linkUser clientId={} username={}", otp.getClientId(), u.getUsername());
        try {
            clientService.linkUser(otp.getClientId(), new LinkUserRequestDTO(u.getUsername()));
            log.info("VERIFY: linkUser OK");

        } catch (FeignException e) {
            log.error("VERIFY: linkUser FAIL status={} msg={} body={}", e.status(), e.getMessage(), e.contentUTF8());
            throw bad("Neuspešno povezivanje klijenta sa korisnikom");
        }

//        otp.setUsed(true);
//        otp.setUser(u);
//        try {
//            otpRepo.save(otp);
//        } catch (Exception e) {
//            log.error("VERIFY: saving OTP failed", e);  // ceo stacktrace
//            throw e;
//        }
        int updated = otpRepo.markUsedAndLink(otp.getId(), u.getUsername());
        if (updated != 1) {
            throw bad("Ažuriranje OTP zapisa nije uspelo");
        }
    }

    private static ResponseStatusException bad(String m) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, m);
    }
    private static ResponseStatusException notFound(String m) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, m);
    }
    private static ResponseStatusException conflict(String m) {
        return new ResponseStatusException(HttpStatus.CONFLICT, m);
    }
    private static String maskEmail(String email) {
        int at = email.indexOf('@');
        if (at <= 1) return "***";
        return email.charAt(0) + "***" + email.substring(at);
    }
}
