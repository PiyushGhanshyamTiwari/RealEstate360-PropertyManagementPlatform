package com.cts.controller;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.cts.dto.LeaseInputDTO;
import com.cts.dto.LeaseOutputDTO;
import com.cts.exception.TenantIdNotFoundException;
import com.cts.exception.UnitIdNotFoundException;
import com.cts.serviceimpl.LeaseServiceImpl;

@ExtendWith(MockitoExtension.class)
class LeaseControllerTest extends AbstractControllerTest {

    @Mock
    private LeaseServiceImpl leaseService;

    @InjectMocks
    private LeaseController leaseController;

    private MockMvc mockMvc;

    private static final String BASE_URL = "/api/v1/lease";

    @BeforeEach
    void setUp() {
        mockMvc = buildMockMvc(leaseController);
    }

    private LeaseOutputDTO sampleLease() {
        return LeaseOutputDTO.builder()
                .leaseId(1)
                .unitId(10)
                .ownerId(2)
                .ownerName("Owner One")
                .tenantId(5)
                .tenantName("Tenant Five")
                .startDate(LocalDate.of(2026, 1, 1))
                .endDate(LocalDate.of(2026, 12, 31))
                .rentAmount(12000.0)
                .depositAmount(24000.0)
                .status("ACTIVE")
                .createdAt(LocalDateTime.now())
                .build();
    }

    private LeaseInputDTO validInput() {
        return LeaseInputDTO.builder()
                .unitId(10)
                .tenantId(5)
                .startDate(LocalDate.of(2026, 1, 1))
                .endDate(LocalDate.of(2026, 12, 31))
                .build();
    }

    @Test
    @DisplayName("POST generate lease -> 201 with created lease")
    void shouldReturn201WhenLeaseGenerated() throws Exception {
        // Arrange
        when(leaseService.leaseGeneration(any(LeaseInputDTO.class))).thenReturn(sampleLease());

        // Act & Assert
        mockMvc.perform(post(BASE_URL + "/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(validInput())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.leaseId").value(1))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.tenantId").value(5));

        verify(leaseService, times(1)).leaseGeneration(any(LeaseInputDTO.class));
    }

    @Test
    @DisplayName("POST generate lease with malformed JSON -> 400 and service not invoked")
    void shouldReturn400WhenBodyIsMalformed() throws Exception {
        // Act & Assert
        mockMvc.perform(post(BASE_URL + "/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ broken"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(leaseService);
    }

    @Test
    @DisplayName("POST generate lease for missing unit -> 404 Not Found")
    void shouldReturn404WhenUnitNotFound() throws Exception {
        // Arrange
        when(leaseService.leaseGeneration(any(LeaseInputDTO.class)))
                .thenThrow(new UnitIdNotFoundException("Unit Id dosen't exists"));

        // Act & Assert
        mockMvc.perform(post(BASE_URL + "/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(validInput())))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Unit Id dosen't exists")));

        verify(leaseService, times(1)).leaseGeneration(any(LeaseInputDTO.class));
    }

    @Test
    @DisplayName("POST generate lease for missing tenant -> 404 Not Found")
    void shouldReturn404WhenTenantNotFound() throws Exception {
        // Arrange
        when(leaseService.leaseGeneration(any(LeaseInputDTO.class)))
                .thenThrow(new TenantIdNotFoundException("Tenant Id dosen't exists"));

        // Act & Assert
        mockMvc.perform(post(BASE_URL + "/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(validInput())))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Tenant Id dosen't exists")));

        verify(leaseService, times(1)).leaseGeneration(any(LeaseInputDTO.class));
    }

    @Test
    @DisplayName("PUT lease status -> 200 with updated lease")
    void shouldReturn200WhenLeaseStatusUpdated() throws Exception {
        // Arrange
        LeaseOutputDTO terminated = sampleLease();
        terminated.setStatus("TERMINATED");
        when(leaseService.updateLeaseStatus(eq(1), eq("TERMINATED"))).thenReturn(terminated);

        // Act & Assert
        mockMvc.perform(put(BASE_URL + "/{leaseId}/{status}", 1, "TERMINATED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("TERMINATED"));

        verify(leaseService, times(1)).updateLeaseStatus(eq(1), eq("TERMINATED"));
    }

    @Test
    @DisplayName("PUT lease status with non-numeric id -> 400")
    void shouldReturn400WhenLeaseIdIsNotNumeric() throws Exception {
        // Act & Assert
        mockMvc.perform(put(BASE_URL + "/{leaseId}/{status}", "abc", "ACTIVE"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(leaseService);
    }

    @Test
    @DisplayName("PUT lease status for missing lease -> service failure surfaced")
    void shouldSurfaceServerErrorWhenLeaseNotFoundOnUpdate() {
        // Arrange
        when(leaseService.updateLeaseStatus(eq(99), eq("ACTIVE")))
                .thenThrow(new RuntimeException("Lease Id not exists"));

        // Act & Assert
        assertRequestFailsWith("Lease Id not exists",
                () -> mockMvc.perform(put(BASE_URL + "/{leaseId}/{status}", 99, "ACTIVE")));

        verify(leaseService, times(1)).updateLeaseStatus(eq(99), eq("ACTIVE"));
    }
}
