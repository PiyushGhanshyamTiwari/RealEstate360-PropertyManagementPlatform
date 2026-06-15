package com.cts.controller;
 
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
 
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
 
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
 
import com.cts.dto.ManagerAssignRequestDTO;
import com.cts.dto.MaintenanceScheduleResponseDTO;
import com.cts.dto.TechnicianStatusUpdateDTO;
import com.cts.dto.TenantIssueRequestDTO;
import com.cts.exception.InvalidStatusTransitionException;
import com.cts.exception.MaintenanceScheduleNotFoundException;
import com.cts.exception.TenantIdNotFoundException;
import com.cts.serviceimpl.MaintenanceScheduleServiceImpl;
 
/**
* Unit tests for {@link MaintenanceScheduleController}.
*/
@ExtendWith(MockitoExtension.class)
class MaintenanceScheduleControllerTest extends AbstractControllerTest {
 
    @Mock
    private MaintenanceScheduleServiceImpl scheduleService;
 
    @InjectMocks
    private MaintenanceScheduleController maintenanceScheduleController;
 
    private MockMvc mockMvc;
 
    private static final String BASE_URL = "/maintenance-schedules";
 
    @BeforeEach
    void setUp() {
        mockMvc = buildMockMvc(maintenanceScheduleController);
    }
 
    private MaintenanceScheduleResponseDTO sampleResponse() {
        MaintenanceScheduleResponseDTO dto = new MaintenanceScheduleResponseDTO();
        dto.setScheduleId(1);
        dto.setTenantId(7);
        dto.setUnitId(101);
        dto.setTechnicianId(3);
        dto.setIssueDescription("Leaking pipe");
        dto.setSeverity("HIGH");
        dto.setStatus("OPEN");
        dto.setScheduledDate(LocalDate.now());
        dto.setCreatedAt(LocalDateTime.now());
        dto.setUpdatedAt(LocalDateTime.now());
        return dto;
    }
 
    private TenantIssueRequestDTO validTenantRequest() {
        TenantIssueRequestDTO dto = new TenantIssueRequestDTO();
        dto.setTenantId(7);
        dto.setUnitId(101);
        dto.setIssueDescription("Leaking pipe");
        return dto;
    }
 
    private ManagerAssignRequestDTO validManagerRequest() {
        ManagerAssignRequestDTO dto = new ManagerAssignRequestDTO();
        dto.setTechnicianId(3);
        dto.setSeverity("HIGH");
        return dto;
    }
 
    private TechnicianStatusUpdateDTO validTechnicianRequest() {
        TechnicianStatusUpdateDTO dto = new TechnicianStatusUpdateDTO();
        dto.setStatus("IN_PROGRESS");
        return dto;
    }
 
    @Test
    @DisplayName("POST schedule by tenant -> 201 with created schedule")
    void shouldReturn201WhenScheduleCreated() throws Exception {
        when(scheduleService.createByTenant(any(TenantIssueRequestDTO.class)))
                .thenReturn(sampleResponse());
 
        mockMvc.perform(post(BASE_URL + "/tenant")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(validTenantRequest())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.scheduleId").value(1))
                .andExpect(jsonPath("$.issueDescription").value("Leaking pipe"));
 
        verify(scheduleService, times(1)).createByTenant(any(TenantIssueRequestDTO.class));
    }
 
    @Test
    @DisplayName("POST schedule by tenant for missing tenant -> 404 Not Found")
    void shouldReturn404WhenTenantNotFound() throws Exception {
        when(scheduleService.createByTenant(any(TenantIssueRequestDTO.class)))
                .thenThrow(new TenantIdNotFoundException("Tenant Id not found"));
 
        mockMvc.perform(post(BASE_URL + "/tenant")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(validTenantRequest())))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Tenant Id not found")));
 
        verify(scheduleService, times(1)).createByTenant(any(TenantIssueRequestDTO.class));
    }
 
    @Test
    @DisplayName("POST schedule with malformed JSON -> 400 and service not invoked")
    void shouldReturn400WhenBodyIsMalformed() throws Exception {
        mockMvc.perform(post(BASE_URL + "/tenant")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ broken json"))
                .andExpect(status().isBadRequest());
 
        verifyNoInteractions(scheduleService);
    }
 
    @Test
    @DisplayName("PUT assign by manager -> 200 with updated schedule")
    void shouldReturn200WhenAssignedByManager() throws Exception {
        when(scheduleService.assignByManager(eq(1), any(ManagerAssignRequestDTO.class)))
                .thenReturn(sampleResponse());
 
        mockMvc.perform(put(BASE_URL + "/{scheduleId}/assign", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(validManagerRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.scheduleId").value(1));
 
        verify(scheduleService, times(1))
                .assignByManager(eq(1), any(ManagerAssignRequestDTO.class));
    }
 
    @Test
    @DisplayName("PUT update by technician -> 200 with updated schedule")
    void shouldReturn200WhenUpdatedByTechnician() throws Exception {
        when(scheduleService.updateByTechnician(eq(1), eq(3), any(TechnicianStatusUpdateDTO.class)))
                .thenReturn(sampleResponse());
 
        mockMvc.perform(put(BASE_URL + "/{scheduleId}/status", 1)
                        .param("technicianId", "3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(validTechnicianRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OPEN"));
 
        verify(scheduleService, times(1))
                .updateByTechnician(eq(1), eq(3), any(TechnicianStatusUpdateDTO.class));
    }
 
    @Test
    @DisplayName("PUT update by technician with invalid status transition -> 400 Bad Request")
    void shouldReturn400WhenInvalidStatusTransition() throws Exception {
        when(scheduleService.updateByTechnician(eq(1), eq(3), any(TechnicianStatusUpdateDTO.class)))
                .thenThrow(new InvalidStatusTransitionException("OPEN", "CLOSED"));
 
        mockMvc.perform(put(BASE_URL + "/{scheduleId}/status", 1)
                        .param("technicianId", "3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(validTechnicianRequest())))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Invalid status transition")));
 
        verify(scheduleService, times(1))
                .updateByTechnician(eq(1), eq(3), any(TechnicianStatusUpdateDTO.class));
    }
 
    @Test
    @DisplayName("GET schedule by id -> 200 with schedule")
    void shouldReturn200WhenScheduleExists() throws Exception {
        when(scheduleService.getScheduleById(1)).thenReturn(sampleResponse());
 
        mockMvc.perform(get(BASE_URL + "/{scheduleId}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.scheduleId").value(1));
 
        verify(scheduleService, times(1)).getScheduleById(1);
    }
 
    @Test
    @DisplayName("GET schedule by id for missing schedule -> 404 Not Found")
    void shouldReturn404WhenScheduleNotFound() throws Exception {
        when(scheduleService.getScheduleById(99))
                .thenThrow(new MaintenanceScheduleNotFoundException(99));
 
        mockMvc.perform(get(BASE_URL + "/{scheduleId}", 99))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Maintenance schedule not found")));
 
        verify(scheduleService, times(1)).getScheduleById(99);
    }
 
    @Test
    @DisplayName("GET schedule with non-numeric id -> 400 and service not invoked")
    void shouldReturn400WhenScheduleIdNotNumeric() throws Exception {
        mockMvc.perform(get(BASE_URL + "/{scheduleId}", "abc"))
                .andExpect(status().isBadRequest());
 
        verifyNoInteractions(scheduleService);
    }
 
    @Test
    @DisplayName("GET all schedules with filters -> 200 with populated page")
    void shouldReturn200WhenSchedulesExistWithFilters() throws Exception {
        Page<MaintenanceScheduleResponseDTO> page =
                new PageImpl<>(List.of(sampleResponse()), PageRequest.of(0, 10), 1);
        when(scheduleService.getAllSchedules(eq("OPEN"), eq("HIGH"), any(Pageable.class)))
                .thenReturn(page);
 
        mockMvc.perform(get(BASE_URL)
                        .param("status", "OPEN")
                        .param("severity", "HIGH")
                        .param("pageNo", "0")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].scheduleId").value(1))
                .andExpect(jsonPath("$.totalElements").value(1));
 
        verify(scheduleService, times(1))
                .getAllSchedules(eq("OPEN"), eq("HIGH"), any(Pageable.class));
    }
 
    @Test
    @DisplayName("GET all schedules without filters -> 200 with empty page")
    void shouldReturn200WithEmptyPageWhenNoSchedules() throws Exception {
        Page<MaintenanceScheduleResponseDTO> page =
                new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 10), 0);
        when(scheduleService.getAllSchedules(isNull(), isNull(), any(Pageable.class)))
                .thenReturn(page);
 
        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(0))
                .andExpect(jsonPath("$.totalElements").value(0));
 
        verify(scheduleService, times(1))
                .getAllSchedules(isNull(), isNull(), any(Pageable.class));
    }
}