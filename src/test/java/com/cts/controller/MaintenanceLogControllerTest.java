package com.cts.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import com.cts.dto.MaintenanceLogRequestDTO;
import com.cts.dto.MaintenanceLogResponseDTO;
import com.cts.service.MaintenanceLogService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.http.MediaType;

@ExtendWith(MockitoExtension.class)
class MaintenanceLogControllerTest {

    @Mock
    private MaintenanceLogService logService;

    @InjectMocks
    private MaintenanceLogController controller;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    
    @Test
    void testAddLog() throws Exception {

        MaintenanceLogRequestDTO request = new MaintenanceLogRequestDTO();

        when(logService.addLog(any()))
                .thenReturn(new MaintenanceLogResponseDTO());

        mockMvc.perform(post("/maintenance-logs/technician")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        verify(logService).addLog(any());
    }

  
    @Test
    void testGetLogsByScheduleId() throws Exception {

        Page<MaintenanceLogResponseDTO> page =
                new PageImpl<>(List.of(new MaintenanceLogResponseDTO()),
                        PageRequest.of(0, 10), 1);

        when(logService.getLogsByScheduleId(eq(1), any()))
                .thenReturn(page);

        mockMvc.perform(get("/maintenance-logs/schedule/{scheduleId}", 1)
                        .param("pageNo", "0")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1));

        verify(logService).getLogsByScheduleId(eq(1), any());
    }

    
    @Test
    void testGetLogsByScheduleIdEmpty() throws Exception {

        Page<MaintenanceLogResponseDTO> page =
                new PageImpl<>(List.of(), PageRequest.of(0, 10), 0);

        when(logService.getLogsByScheduleId(eq(1), any()))
                .thenReturn(page);

        mockMvc.perform(get("/maintenance-logs/schedule/{scheduleId}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(0));
    }

    
    @Test
    void testGetLogsByScheduleIdWithDifferentPaging() throws Exception {

        Page<MaintenanceLogResponseDTO> page =
                new PageImpl<>(List.of(new MaintenanceLogResponseDTO()),
                        PageRequest.of(1, 5), 1);

        when(logService.getLogsByScheduleId(eq(2), any()))
                .thenReturn(page);

        mockMvc.perform(get("/maintenance-logs/schedule/{scheduleId}", 2)
                        .param("pageNo", "1")
                        .param("pageSize", "5"))
                .andExpect(status().isOk());

        verify(logService).getLogsByScheduleId(eq(2), any());
    }
}
