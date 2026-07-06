package com.cts.serviceimpl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.cts.dto.AuditLogRequestDTO;
import com.cts.dto.AuditLogResponseDTO;
import com.cts.entity.AuditLog;
import com.cts.mapper.AuditLogMapper;
import com.cts.repository.AuditLogRepository;
import com.cts.service.AuditLogService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final AuditLogMapper auditLogMapper;

    @Override
    @Async
    public AuditLogResponseDTO logAction(AuditLogRequestDTO requestDTO) {
        AuditLog entity = auditLogMapper.convertToAuditLog(requestDTO);
        AuditLog saved = auditLogRepository.save(entity);
        return auditLogMapper.convertToAuditLogResponseDTO(saved);
    }

    @Override
    public List<AuditLogResponseDTO> getAllLogs(String logType,String logValue) {
        if (logType == null) {
            return auditLogRepository.findAll()
                    .stream()
                    .map(auditLogMapper::convertToAuditLogResponseDTO)
                    .collect(Collectors.toList());
        } else {
            switch (logType) {
                case "USER":
                    return getLogsByUserId(Integer.parseInt(logValue));
                case "ACTION":
                    return getLogsByAction(logValue);
                case "RESOURCE":
                    return getLogsByResourceType(logValue);

            }

        }
        return null;
    }

    @Override
    public AuditLogResponseDTO getLogById(Long auditId) {
        AuditLog entity = auditLogRepository.findById(auditId)
                .orElseThrow(() -> new RuntimeException("AuditLog not found with id: " + auditId));
        return auditLogMapper.convertToAuditLogResponseDTO(entity);
    }

    @Override
    public List<AuditLogResponseDTO> getLogsByUserId(Integer userId) {
        return auditLogRepository.findByUserId(userId)
                .stream()
                .map(log -> auditLogMapper.convertToAuditLogResponseDTO(log))
                .collect(Collectors.toList());
    }

    @Override
    public List<AuditLogResponseDTO> getLogsByAction(String action) {
        return auditLogRepository.findByAction(action)
                .stream()
                .map(log -> auditLogMapper.convertToAuditLogResponseDTO(log))
                .collect(Collectors.toList());
    }

    @Override
    public List<AuditLogResponseDTO> getLogsByResourceType(String resourceType) {
        return auditLogRepository.findByResourceType(resourceType)
                .stream()
                .map(log -> auditLogMapper.convertToAuditLogResponseDTO(log))
                .collect(Collectors.toList());
    }
}
