package com.cts.service;

import java.util.List;

import com.cts.dto.AuditLogRequestDTO;
import com.cts.dto.AuditLogResponseDTO;

public interface AuditLogService {

    AuditLogResponseDTO logAction(AuditLogRequestDTO requestDTO);

    List<AuditLogResponseDTO> getAllLogs();

    AuditLogResponseDTO getLogById(Long auditId);

    List<AuditLogResponseDTO> getLogsByUserId(Integer userId);

    List<AuditLogResponseDTO> getLogsByAction(String action);

    List<AuditLogResponseDTO> getLogsByResourceType(String resourceType);
}
