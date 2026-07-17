package com.cts.serviceimpl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import com.cts.dto.AuditLogRequestDTO;
import com.cts.dto.AuditLogResponseDTO;
import com.cts.entity.AuditLog;
import com.cts.mapper.AuditLogMapper;
import com.cts.repository.AuditLogRepository;

@ExtendWith(MockitoExtension.class)
class AuditLogServiceImplTest {

    @Mock
    private AuditLogRepository auditLogRepository;

    @Mock
    private AuditLogMapper auditLogMapper;

    @Spy
    @InjectMocks
    private AuditLogServiceImpl auditLogService;

    @Test
    void testLogAction() {

        AuditLogRequestDTO requestDTO = new AuditLogRequestDTO();
        AuditLog entity = new AuditLog();
        AuditLog savedEntity = new AuditLog();
        AuditLogResponseDTO responseDTO = new AuditLogResponseDTO();

        when(auditLogMapper.convertToAuditLog(requestDTO)).thenReturn(entity);
        when(auditLogRepository.save(entity)).thenReturn(savedEntity);
        when(auditLogMapper.convertToAuditLogResponseDTO(savedEntity))
                .thenReturn(responseDTO);

        AuditLogResponseDTO result = auditLogService.logAction(requestDTO);

        assertNotNull(result);
        assertEquals(responseDTO, result);

        verify(auditLogMapper).convertToAuditLog(requestDTO);
        verify(auditLogRepository).save(entity);
        verify(auditLogMapper).convertToAuditLogResponseDTO(savedEntity);
    }

    @Test
    void testGetAllLogs_NullLogType() {

        AuditLog auditLog = new AuditLog();
        AuditLogResponseDTO dto = new AuditLogResponseDTO();

        when(auditLogRepository.findAll()).thenReturn(List.of(auditLog));
        when(auditLogMapper.convertToAuditLogResponseDTO(auditLog))
                .thenReturn(dto);

        List<AuditLogResponseDTO> result =
                auditLogService.getAllLogs(null, null);

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(auditLogRepository).findAll();
    }

    @Test
    void testGetAllLogs_User() {

        AuditLogResponseDTO dto = new AuditLogResponseDTO();

        doReturn(List.of(dto))
                .when(auditLogService)
                .getLogsByUserId(1);

        List<AuditLogResponseDTO> result =
                auditLogService.getAllLogs("USER", "1");

        assertEquals(1, result.size());

        verify(auditLogService).getLogsByUserId(1);
    }

    @Test
    void testGetAllLogs_Action() {

        AuditLogResponseDTO dto = new AuditLogResponseDTO();

        doReturn(List.of(dto))
                .when(auditLogService)
                .getLogsByAction("CREATE");

        List<AuditLogResponseDTO> result =
                auditLogService.getAllLogs("ACTION", "CREATE");

        assertEquals(1, result.size());

        verify(auditLogService).getLogsByAction("CREATE");
    }

    @Test
    void testGetAllLogs_Resource() {

        AuditLogResponseDTO dto = new AuditLogResponseDTO();

        doReturn(List.of(dto))
                .when(auditLogService)
                .getLogsByResourceType("PROPERTY");

        List<AuditLogResponseDTO> result =
                auditLogService.getAllLogs("RESOURCE", "PROPERTY");

        assertEquals(1, result.size());

        verify(auditLogService).getLogsByResourceType("PROPERTY");
    }

    @Test
    void testGetAllLogs_InvalidType() {

        List<AuditLogResponseDTO> result =
                auditLogService.getAllLogs("INVALID", "TEST");

        assertNull(result);
    }

    @Test
    void testGetLogById_Success() {

        Long auditId = 1L;

        AuditLog auditLog = new AuditLog();
        AuditLogResponseDTO dto = new AuditLogResponseDTO();

        when(auditLogRepository.findById(auditId))
                .thenReturn(Optional.of(auditLog));

        when(auditLogMapper.convertToAuditLogResponseDTO(auditLog))
                .thenReturn(dto);

        AuditLogResponseDTO result =
                auditLogService.getLogById(auditId);

        assertEquals(dto, result);
    }

    @Test
    void testGetLogById_NotFound() {

        Long auditId = 1L;

        when(auditLogRepository.findById(auditId))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> auditLogService.getLogById(auditId));

        assertEquals(
                "AuditLog not found with id: 1",
                exception.getMessage());
    }

    @Test
    void testGetLogsByUserId() {

        AuditLog log = new AuditLog();
        AuditLogResponseDTO dto = new AuditLogResponseDTO();

        when(auditLogRepository.findByUserId(1))
                .thenReturn(List.of(log));

        when(auditLogMapper.convertToAuditLogResponseDTO(log))
                .thenReturn(dto);

        List<AuditLogResponseDTO> result =
                auditLogService.getLogsByUserId(1);

        assertEquals(1, result.size());
    }

    @Test
    void testGetLogsByAction() {

        AuditLog log = new AuditLog();
        AuditLogResponseDTO dto = new AuditLogResponseDTO();

        when(auditLogRepository.findByAction("CREATE"))
                .thenReturn(List.of(log));

        when(auditLogMapper.convertToAuditLogResponseDTO(log))
                .thenReturn(dto);

        List<AuditLogResponseDTO> result =
                auditLogService.getLogsByAction("CREATE");

        assertEquals(1, result.size());
    }

    @Test
    void testGetLogsByResourceType() {

        AuditLog log = new AuditLog();
        AuditLogResponseDTO dto = new AuditLogResponseDTO();

        when(auditLogRepository.findByResourceType("PROPERTY"))
                .thenReturn(List.of(log));

        when(auditLogMapper.convertToAuditLogResponseDTO(log))
                .thenReturn(dto);

        List<AuditLogResponseDTO> result =
                auditLogService.getLogsByResourceType("PROPERTY");

        assertEquals(1, result.size());
    }
}