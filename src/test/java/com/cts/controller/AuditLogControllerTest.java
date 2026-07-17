package com.cts.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.cts.dto.AuditLogResponseDTO;
import com.cts.service.AuditLogService;

@ExtendWith(MockitoExtension.class)
class AuditLogControllerTest {

    @Mock
    private AuditLogService auditLogService;

    @InjectMocks
    private AuditLogController auditLogController;

    @Test
    void testGetAll_WithFilters() {

        String logType = "ACTION";
        String logValue = "CREATE";

        List<AuditLogResponseDTO> logs = new ArrayList<>();
        logs.add(new AuditLogResponseDTO());

        when(auditLogService.getAllLogs(logType, logValue))
                .thenReturn(logs);

        ResponseEntity<List<AuditLogResponseDTO>> response =
                auditLogController.getAll(logType, logValue);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(logs, response.getBody());

        verify(auditLogService).getAllLogs(logType, logValue);
    }

    @Test
    void testGetAll_WithNullFilters() {

        List<AuditLogResponseDTO> logs = new ArrayList<>();

        when(auditLogService.getAllLogs(null, null))
                .thenReturn(logs);

        ResponseEntity<List<AuditLogResponseDTO>> response =
                auditLogController.getAll(null, null);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(logs, response.getBody());

        verify(auditLogService).getAllLogs(null, null);
    }
}