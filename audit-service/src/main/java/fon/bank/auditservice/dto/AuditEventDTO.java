package fon.bank.auditservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AuditEventDTO {
    @NotBlank private String service;
    @NotBlank private String action;
    @NotBlank private String outcome;

    private String principal;
    private String ip;
    private String userAgent;
    private String resourceType;
    private String resourceId;
    private String correlationId;
    private String httpMethod;
    private String httpPath;
    private Integer httpStatus;
    private Integer durationMs;
    private String checksJson;
    private String detailsJson;
    private String tagsJson;
}
