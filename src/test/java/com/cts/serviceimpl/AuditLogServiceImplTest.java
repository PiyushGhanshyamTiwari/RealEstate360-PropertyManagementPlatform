package com.cts.serviceimpl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.cts.dto.AuditLogRequestDTO;
import com.cts.dto.AuditLogResponseDTO;
import com.cts.entity.AuditLog;
import com.cts.mapper.AuditLogMapper;
import com.cts.repository.AuditLogRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AuditLogServiceImplTest {

    @Mock
    private AuditLogRepository auditLogRepository;

    @Mock
    private AuditLogMapper auditLogMapper;

    @InjectMocks
    private AuditLogServiceImpl service;

    private AuditLog auditLog;
    private AuditLogResponseDTO responseDTO;

    @BeforeEach
    void setup() {
        auditLog = new AuditLog();
        auditLog.setAuditId(1L);
        auditLog.setUserId(10);
        auditLog.setAction("LOGIN_USER");
        auditLog.setResourceType("User");

        responseDTO = new AuditLogResponseDTO();
        responseDTO.setAuditId(1L);
    }

    @Test
    void testLogAction() {
        AuditLogRequestDTO request = AuditLogRequestDTO.builder()
                .userId(10)
                .action("LOGIN_USER")
                .resourceType("User")
                .build();

        when(auditLogMapper.convertToAuditLog(request)).thenReturn(auditLog);
        when(auditLogRepository.save(auditLog)).thenReturn(auditLog);
        when(auditLogMapper.convertToAuditLogResponseDTO(auditLog)).thenReturn(responseDTO);

        AuditLogResponseDTO result = service.logAction(request);

        assertNotNull(result);
        verify(auditLogRepository).save(auditLog);
    }

    @Test
    void testGetAllLogs() {
        when(auditLogRepository.findAll()).thenReturn(List.of(auditLog));
        when(auditLogMapper.convertToAuditLogResponseDTO(auditLog)).thenReturn(responseDTO);

        List<AuditLogResponseDTO> result = service.getAllLogs();

        assertEquals(1, result.size());
    }

    @Test
    void testGetAllLogsEmpty() {
        when(auditLogRepository.findAll()).thenReturn(Collections.emptyList());

        assertTrue(service.getAllLogs().isEmpty());
    }

    @Test
    void testGetLogByIdSuccess() {
        when(auditLogRepository.findById(1L)).thenReturn(Optional.of(auditLog));
        when(auditLogMapper.convertToAuditLogResponseDTO(auditLog)).thenReturn(responseDTO);

        AuditLogResponseDTO result = service.getLogById(1L);

        assertNotNull(result);
    }

    @Test
    void testGetLogByIdNotFound() {
        when(auditLogRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.getLogById(1L));

        assertTrue(ex.getMessage().contains("AuditLog not found"));
    }

    @Test
    void testGetLogsByUserId() {
        when(auditLogRepository.findByUserId(10)).thenReturn(List.of(auditLog));
        when(auditLogMapper.convertToAuditLogResponseDTO(auditLog)).thenReturn(responseDTO);

        List<AuditLogResponseDTO> result = service.getLogsByUserId(10);

        assertEquals(1, result.size());
    }

    @Test
    void testGetLogsByAction() {
        when(auditLogRepository.findByAction("LOGIN_USER")).thenReturn(List.of(auditLog));
        when(auditLogMapper.convertToAuditLogResponseDTO(auditLog)).thenReturn(responseDTO);

        List<AuditLogResponseDTO> result = service.getLogsByAction("LOGIN_USER");

        assertEquals(1, result.size());
    }

    @Test
    void testGetLogsByResourceType() {
        when(auditLogRepository.findByResourceType("User")).thenReturn(List.of(auditLog));
        when(auditLogMapper.convertToAuditLogResponseDTO(auditLog)).thenReturn(responseDTO);

        List<AuditLogResponseDTO> result = service.getLogsByResourceType("User");

        assertEquals(1, result.size());
    }
}
