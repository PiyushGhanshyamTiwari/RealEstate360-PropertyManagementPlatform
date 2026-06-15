package com.cts.controller;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

import com.cts.dto.MaintenanceLogRequestDTO;
import com.cts.dto.MaintenanceLogResponseDTO;
import com.cts.exception.MaintenanceScheduleNotFoundException;
import com.cts.serviceimpl.MaintenanceLogServiceImpl;

/**
 * Unit tests for {@link MaintenanceLogController}.
 *
 * <p>The concrete {@link MaintenanceLogServiceImpl} is mocked (not the interface) and injected into
 * the controller so the controller is exercised exactly as wired in production.
 */
@ExtendWith(MockitoExtension.class)
class MaintenanceLogControllerTest extends AbstractControllerTest {

    @Mock
    private MaintenanceLogServiceImpl logService;

    @InjectMocks
    private MaintenanceLogController maintenanceLogController;

    private MockMvc mockMvc;

    private static final String BASE_URL = "/maintenance-logs";

    @BeforeEach
    void setUp() {
        mockMvc = buildMockMvc(maintenanceLogController);
    }

    private MaintenanceLogResponseDTO sampleResponse() {
        MaintenanceLogResponseDTO dto = new MaintenanceLogResponseDTO();
        dto.setLogId(1);
        dto.setScheduleId(5);
        dto.setRemarks("Replaced faulty wiring");
        dto.setLogDate(LocalDateTime.now());
        return dto;
    }

    private MaintenanceLogRequestDTO validRequest() {
        MaintenanceLogRequestDTO dto = new MaintenanceLogRequestDTO();
        dto.setScheduleId(5);
        dto.setRemarks("Replaced faulty wiring");
        return dto;
    }

    @Test
    @DisplayName("POST log -> 201 with created log")
    void shouldReturn201WhenLogCreated() throws Exception {
        // Arrange
        when(logService.addLog(any(MaintenanceLogRequestDTO.class))).thenReturn(sampleResponse());

        // Act & Assert
        mockMvc.perform(post(BASE_URL + "/technician")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(validRequest())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.logId").value(1))
                .andExpect(jsonPath("$.scheduleId").value(5))
                .andExpect(jsonPath("$.remarks").value("Replaced faulty wiring"));

        verify(logService, times(1)).addLog(any(MaintenanceLogRequestDTO.class));
    }

    @Test
    @DisplayName("POST log for missing schedule -> 404 Not Found")
    void shouldReturn404WhenScheduleNotFound() throws Exception {
        // Arrange
        when(logService.addLog(any(MaintenanceLogRequestDTO.class)))
                .thenThrow(new MaintenanceScheduleNotFoundException(5));

        // Act & Assert
        mockMvc.perform(post(BASE_URL + "/technician")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(validRequest())))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Maintenance schedule not found")));

        verify(logService, times(1)).addLog(any(MaintenanceLogRequestDTO.class));
    }

    @Test
    @DisplayName("POST log with malformed JSON -> 400 and service not invoked")
    void shouldReturn400WhenBodyIsMalformed() throws Exception {
        // Act & Assert
        mockMvc.perform(post(BASE_URL + "/technician")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ broken json"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(logService);
    }

    @Test
    @DisplayName("GET logs by schedule -> 200 with populated page")
    void shouldReturn200WhenLogsExist() throws Exception {
        // Arrange
        Page<MaintenanceLogResponseDTO> page =
                new PageImpl<>(List.of(sampleResponse()), PageRequest.of(0, 10), 1);
        when(logService.getLogsByScheduleId(eq(5), any(Pageable.class))).thenReturn(page);

        // Act & Assert
        mockMvc.perform(get(BASE_URL + "/schedule/{scheduleId}", 5)
                        .param("pageNo", "0")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].logId").value(1))
                .andExpect(jsonPath("$.content[0].scheduleId").value(5))
                .andExpect(jsonPath("$.totalElements").value(1));

        verify(logService, times(1)).getLogsByScheduleId(eq(5), any(Pageable.class));
    }

    @Test
    @DisplayName("GET logs by schedule when none -> 200 with empty page")
    void shouldReturn200WithEmptyPageWhenNoLogs() throws Exception {
        // Arrange
        Page<MaintenanceLogResponseDTO> page =
                new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 10), 0);
        when(logService.getLogsByScheduleId(eq(99), any(Pageable.class))).thenReturn(page);

        // Act & Assert
        mockMvc.perform(get(BASE_URL + "/schedule/{scheduleId}", 99))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(0))
                .andExpect(jsonPath("$.totalElements").value(0));

        verify(logService, times(1)).getLogsByScheduleId(eq(99), any(Pageable.class));
    }

    @Test
    @DisplayName("GET logs with non-numeric schedule id -> 400 and service not invoked")
    void shouldReturn400WhenScheduleIdNotNumeric() throws Exception {
        // Act & Assert
        mockMvc.perform(get(BASE_URL + "/schedule/{scheduleId}", "abc"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(logService);
    }
}
