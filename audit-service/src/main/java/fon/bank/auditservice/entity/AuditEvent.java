package fon.bank.auditservice.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_event")
@Data
public class AuditEvent {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime ts;
    private String service;
    private String action;
    private String principal;
    private String outcome;
    private String ip;
    private String userAgent;
    private String resourceType;
    private String resourceId;
    private String correlationId;
    private String httpMethod;
    private String httpPath;
    private Integer httpStatus;
    private Integer durationMs;

    @Column(columnDefinition = "json")
    private String checksJson;

    @Column(columnDefinition = "json")
    private String detailsJson;

    @Column(columnDefinition = "json")
    private String tagsJson;
}
