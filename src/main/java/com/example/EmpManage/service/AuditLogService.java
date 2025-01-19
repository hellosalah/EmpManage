package com.example.EmpManage.service;

import com.example.EmpManage.model.AuditLog;
import com.example.EmpManage.repository.AuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AuditLogService {
    @Autowired
    private AuditLogRepository auditLogRepository;
    public void logAction(Long employeeId, Long performedBy, String action) {
        AuditLog log = new AuditLog();
        log.setEmployeeId(employeeId);
        log.setPerformedBy(performedBy);
        log.setAction(action);
        log.setTimestamp(LocalDateTime.now());
        auditLogRepository.save(log);
    }

    public List<AuditLog> getAllAuditLogs() {
        return auditLogRepository.findAll();
    }
}
