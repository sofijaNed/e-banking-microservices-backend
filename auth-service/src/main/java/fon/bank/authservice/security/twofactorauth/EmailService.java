package fon.bank.authservice.security.twofactorauth;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;
    @Value("${spring.mail.username:}")
    private String from;

    @Value("${spring.mail.properties.mail.brand:MojaBanka}")
    private String brand;

    @Value("${spring.mail.properties.mail.otp.subject:OTP kod}")
    private String otpSubject;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendOtp(@NonNull String to, @NonNull String otpCode) {
        String subject = otpSubject;
        String textBody = buildPlainOtpBody(otpCode);
        String htmlBody = buildHtmlOtpBody(otpCode);

        try {
            sendHtmlEmail(to, subject, htmlBody);
        } catch (Exception ex) {
            sendPlainEmail(to, subject, textBody);
        }
    }

    public void sendPlainEmail(@NonNull String to, @NonNull String subject, @NonNull String text) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(to);
        msg.setSubject(subject);
        msg.setText(text);
        if (from != null && !from.isBlank()) {
            msg.setFrom(from);
        }
        mailSender.send(msg);
    }

    public void sendOtpEmail(@NonNull String to, @NonNull String subject, @NonNull String text) {
        sendPlainEmail(to, subject, text);
    }

    public void sendTestEmail(@NonNull String to) {
        sendPlainEmail(to, "Test Mail", "Ako vidiš ovaj mail, slanje radi.");
    }

    private void sendHtmlEmail(@NonNull String to, @NonNull String subject, @NonNull String html)
            throws MessagingException {
        MimeMessage mime = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mime, "UTF-8");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(html, true);
        if (from != null && !from.isBlank()) {
            helper.setFrom(from);
        }
        mailSender.send(mime);
    }

    private String buildPlainOtpBody(String otp) {
        return """
               Vaš OTP kod: %s

               Ovaj kod važi 10 minuta. Ako niste vi pokrenuli zahtev, ignorišite ovu poruku.

               %s tim
               """.formatted(otp, brand);
    }

    private String buildHtmlOtpBody(String otp) {
        return """
            <div style="font-family:Arial,sans-serif;font-size:14px;line-height:1.5">
              <p>Zdravo,</p>
              <p>Vaš <b>OTP kod</b> je:</p>
              <p style="font-size:22px;font-weight:700;letter-spacing:2px;">%s</p>
              <p>Ovaj kod važi <b>10 minuta</b>. Ako niste vi pokrenuli zahtev, slobodno ignorišite ovu poruku.</p>
              <hr/>
              <p style="color:#666;">%s tim</p>
            </div>
            """.formatted(otp, brand);
    }
}
