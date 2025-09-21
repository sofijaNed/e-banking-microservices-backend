package fon.bank.auditservice.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import fon.bank.auditservice.entity.AuditEvent;

public interface AuditEventRepository extends JpaRepository<AuditEvent, Long> {}
