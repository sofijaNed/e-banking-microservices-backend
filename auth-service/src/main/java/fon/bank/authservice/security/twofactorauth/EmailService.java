package fon.bank.authservice.security.twofactorauth;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.slf4j.MDC;


import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    @Value("${app.correlation.header:X-Correlation-ID}")
    private String correlationHeader;
    @Value("${spring.mail.username:}")
    private String from;

    @Value("${spring.mail.properties.mail.brand:MojaBanka}")
    private String brand;

    @Value("${spring.mail.properties.mail.otp.subject:OTP kod}")
    private String otpSubject;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Async("mailExecutor")
    public void sendOtp(@NonNull String to, @NonNull String otpCode) {
        final String cid = Optional.ofNullable(MDC.get("cid"))
                .filter(s -> !s.isBlank())
                .orElse(UUID.randomUUID().toString());
        final long epochMs = Instant.now().toEpochMilli();

        final String subject = otpSubject + " [cid: " + cid + "]";
        final String htmlBody = buildHtmlOtpBody(otpCode);
        final String textBody = buildPlainOtpBody(otpCode);

        try {
            sendMimeEmailWithHeaders(to, subject, htmlBody, true, cid, epochMs);
        } catch (Exception ex) {
            try {
                sendMimeEmailWithHeaders(to, subject, textBody, false, cid, epochMs);
            } catch (MessagingException ignored) {
                sendPlainEmail(to, subject, textBody);
            }
        }
    }

    private void sendMimeEmailWithHeaders(@NonNull String to,
                                          @NonNull String subject,
                                          @NonNull String content,
                                          boolean isHtml,
                                          @NonNull String cid,
                                          long epochMs) throws MessagingException {
        MimeMessage mime = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mime, "UTF-8");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(content, isHtml);
        if (from != null && !from.isBlank()) {
            helper.setFrom(from);
        }
        mime.addHeader(correlationHeader, cid);
        mime.addHeader("X-OTP-Sent-Epoch", String.valueOf(epochMs));
        mailSender.send(mime);
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
