package com.cts.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import com.cts.dto.AuditLogResponseDTO;
import com.cts.service.AuditLogService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class AuditLogControllerTest {

    @Mock
    private AuditLogService auditLogService;

    @InjectMocks
    private AuditLogController controller;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void testGetAll() throws Exception {
        when(auditLogService.getAllLogs())
                .thenReturn(List.of(new AuditLogResponseDTO()));

        mockMvc.perform(get("/api/v1/audit-logs/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        verify(auditLogService).getAllLogs();
    }

    @Test
    void testGetAllEmpty() throws Exception {
        when(auditLogService.getAllLogs())
                .thenReturn(List.of());

        mockMvc.perform(get("/api/v1/audit-logs/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void testGetById() throws Exception {
        when(auditLogService.getLogById(1L))
                .thenReturn(new AuditLogResponseDTO());

        mockMvc.perform(get("/api/v1/audit-logs/{auditId}", 1L))
                .andExpect(status().isOk());

        verify(auditLogService).getLogById(1L);
    }

    @Test
    void testGetByUser() throws Exception {
        when(auditLogService.getLogsByUserId(10))
                .thenReturn(List.of(new AuditLogResponseDTO()));

        mockMvc.perform(get("/api/v1/audit-logs/user/{userId}", 10))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        verify(auditLogService).getLogsByUserId(10);
    }

    @Test
    void testGetByAction() throws Exception {
        when(auditLogService.getLogsByAction("LOGIN_USER"))
                .thenReturn(List.of(new AuditLogResponseDTO()));

        mockMvc.perform(get("/api/v1/audit-logs/action/{action}", "LOGIN_USER"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        verify(auditLogService).getLogsByAction("LOGIN_USER");
    }

    @Test
    void testGetByResourceType() throws Exception {
        when(auditLogService.getLogsByResourceType("User"))
                .thenReturn(List.of(new AuditLogResponseDTO()));

        mockMvc.perform(get("/api/v1/audit-logs/resource/{resourceType}", "User"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        verify(auditLogService).getLogsByResourceType("User");
    }
}
