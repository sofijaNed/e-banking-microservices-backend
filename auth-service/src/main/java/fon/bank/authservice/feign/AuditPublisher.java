package fon.bank.authservice.feign;

import fon.bank.authservice.dto.AuditEventDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuditPublisher {
    private final AuditClient client;

    @Async
    public void send(AuditEventDTO dto) {
        try { client.write(dto); } catch (Exception ignored) { }
    }
}
