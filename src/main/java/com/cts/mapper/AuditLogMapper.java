package com.cts.mapper;

import org.springframework.stereotype.Component;

import com.cts.dto.AuditLogRequestDTO;
import com.cts.dto.AuditLogResponseDTO;
import com.cts.entity.AuditLog;

@Component
public class AuditLogMapper {

    public AuditLog convertToAuditLog(AuditLogRequestDTO dto) {
        AuditLog auditLog = new AuditLog();
        auditLog.setUserId(dto.getUserId());
        auditLog.setAction(dto.getAction());
        auditLog.setResourceType(dto.getResourceType());
        auditLog.setResourceId(dto.getResourceId());
        auditLog.setDetails(dto.getDetails());
        auditLog.setStatus(dto.getStatus() != null ? dto.getStatus() : "SUCCESS");
        return auditLog;
    }

    public AuditLogResponseDTO convertToAuditLogResponseDTO(AuditLog auditLog) {
        AuditLogResponseDTO response = new AuditLogResponseDTO();
        response.setAuditId(auditLog.getAuditId());
        response.setUserId(auditLog.getUserId());
        response.setAction(auditLog.getAction());
        response.setResourceType(auditLog.getResourceType());
        response.setResourceId(auditLog.getResourceId());
        response.setDetails(auditLog.getDetails());
        response.setStatus(auditLog.getStatus());
        response.setTimestamp(auditLog.getTimestamp());
        return response;
    }
}
