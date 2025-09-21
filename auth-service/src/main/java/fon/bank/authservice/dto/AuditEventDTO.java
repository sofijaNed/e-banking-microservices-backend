package fon.bank.authservice.dto;

import lombok.Data;

@Data
public class AuditEventDTO {
    private String service;
    private String action;
    private String outcome;
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
