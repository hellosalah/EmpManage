package com.example.EmpManage.controller;

import com.example.EmpManage.model.AuditLog;
import com.example.EmpManage.service.AuditLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/auditLogs")
public class AuditLogController {

    @Autowired
    private AuditLogService auditLogService;

    @GetMapping
    public List<AuditLog> getAuditLogs() {
        return auditLogService.getAllAuditLogs();
    }
}

