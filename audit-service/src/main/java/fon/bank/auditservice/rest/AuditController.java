package fon.bank.auditservice.rest;

import fon.bank.auditservice.dto.AuditEventDTO;
import fon.bank.auditservice.entity.AuditEvent;
import fon.bank.auditservice.dao.AuditEventRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/audit")
@RequiredArgsConstructor
public class AuditController {

    private final AuditEventRepository repo;

    @PostMapping("/events")
    public ResponseEntity<Void> write(@Valid @RequestBody AuditEventDTO dto) {
        AuditEvent e = new AuditEvent();
        e.setTs(LocalDateTime.now());
        e.setService(dto.getService());
        e.setAction(dto.getAction());
        e.setOutcome(dto.getOutcome());
        e.setPrincipal(dto.getPrincipal());
        e.setIp(dto.getIp());
        e.setUserAgent(dto.getUserAgent());
        e.setResourceType(dto.getResourceType());
        e.setResourceId(dto.getResourceId());
        e.setCorrelationId(org.slf4j.MDC.get("cid"));
        e.setHttpMethod(dto.getHttpMethod());
        e.setHttpPath(dto.getHttpPath());
        e.setHttpStatus(dto.getHttpStatus());
        e.setDurationMs(dto.getDurationMs());
        e.setChecksJson(dto.getChecksJson());
        e.setDetailsJson(dto.getDetailsJson());
        e.setTagsJson(dto.getTagsJson());
        repo.save(e);
        return ResponseEntity.accepted().build();
    }
}
