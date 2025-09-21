package fon.bank.authservice.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import fon.bank.authservice.dto.AuditEventDTO;

@FeignClient(
        name = "audit-service",
        url = "http://audit-service:8099",
        configuration = FeignConfig.class
)
public interface AuditClient {
    @PostMapping("/audit/events")
    void write(@RequestBody AuditEventDTO dto);
}