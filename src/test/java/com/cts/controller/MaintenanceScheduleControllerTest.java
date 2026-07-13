package com.cts.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import com.cts.dto.*;
import com.cts.service.MaintenanceScheduleService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.http.MediaType;

@ExtendWith(MockitoExtension.class)
class MaintenanceScheduleControllerTest {

    @Mock
    private MaintenanceScheduleService scheduleService;

    @InjectMocks
    private MaintenanceScheduleController controller;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    
    @Test
    void testCreateByTenant() throws Exception {

        TenantIssueRequestDTO request = new TenantIssueRequestDTO();

        when(scheduleService.createByTenant(any()))
                .thenReturn(new MaintenanceScheduleResponseDTO());

        mockMvc.perform(post("/maintenance-schedules/tenant")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        verify(scheduleService).createByTenant(any());
    }

    
    @Test
    void testAssignByManager() throws Exception {

        ManagerAssignRequestDTO request = new ManagerAssignRequestDTO();

        when(scheduleService.assignByManager(eq(1), any()))
                .thenReturn(new MaintenanceScheduleResponseDTO());

        mockMvc.perform(put("/maintenance-schedules/{scheduleId}/assign", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(scheduleService).assignByManager(eq(1), any());
    }

    @Test
    void testUpdateByTechnician() throws Exception {

        TechnicianStatusUpdateDTO request = new TechnicianStatusUpdateDTO();
        request.setStatus("IN_PROGRESS");

        when(scheduleService.updateByTechnician(eq(1), eq(10), any()))
                .thenReturn(new MaintenanceScheduleResponseDTO());

        mockMvc.perform(put("/maintenance-schedules/{scheduleId}/status", 1)
                        .param("userId", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(scheduleService).updateByTechnician(eq(1), eq(10), any());
    }

    
    @Test
    void testGetScheduleById() throws Exception {

        when(scheduleService.getScheduleById(1))
                .thenReturn(new MaintenanceScheduleResponseDTO());

        mockMvc.perform(get("/maintenance-schedules/{scheduleId}", 1))
                .andExpect(status().isOk());

        verify(scheduleService).getScheduleById(1);
    }

   
    @Test
    void testGetAllSchedules() throws Exception {

        when(scheduleService.getAllSchedules(null, null))
                .thenReturn(List.of(new MaintenanceScheduleResponseDTO()));

        mockMvc.perform(get("/maintenance-schedules"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        verify(scheduleService).getAllSchedules(null, null);
    }

    
    @Test
    void testGetAllSchedulesWithFilters() throws Exception {

        when(scheduleService.getAllSchedules("OPEN", "HIGH"))
                .thenReturn(List.of(new MaintenanceScheduleResponseDTO()));

        mockMvc.perform(get("/maintenance-schedules")
                        .param("status", "OPEN")
                        .param("severity", "HIGH"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        verify(scheduleService).getAllSchedules("OPEN", "HIGH");
    }

    
    @Test
    void testGetAllSchedulesEmpty() throws Exception {

        when(scheduleService.getAllSchedules(null, null))
                .thenReturn(List.of());

        mockMvc.perform(get("/maintenance-schedules"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}