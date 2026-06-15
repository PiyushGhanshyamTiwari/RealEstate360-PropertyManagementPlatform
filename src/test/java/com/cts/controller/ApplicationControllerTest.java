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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.cts.dto.ApplicationInputDTO;
import com.cts.dto.ApplicationOutputDTO;

import com.cts.exception.UnitIdNotFoundException;
import com.cts.exception.UserIdNotFoundException;
import com.cts.serviceimpl.ApplicationServiceImpl;

@ExtendWith(MockitoExtension.class)
class ApplicationControllerTest extends AbstractControllerTest {

    @Mock
    private ApplicationServiceImpl applicationService;

    @InjectMocks
    private ApplicationController applicationController;

    private MockMvc mockMvc;

    private static final String BASE_URL = "/api/v1/application";

    @BeforeEach
    void setUp() {
        mockMvc = buildMockMvc(applicationController);
    }

    private ApplicationOutputDTO sampleApplication() {
        return ApplicationOutputDTO.builder()
                .applicationId(1)
                .unitId(10)
                .userId(20)
                .submittedAt(LocalDateTime.now())
                .status("SUBMITTED")
                .type("LEASE")
                .propertyName("Palm Residency")
                .address("12 Main St")
                .city("Chennai")
                .build();
    }

    private ApplicationInputDTO validInput() {
        return ApplicationInputDTO.builder().unitId(10).userId(20).build();
    }

    @Test
    @DisplayName("POST application -> 201 with created application")
    void shouldReturn201WhenApplicationSubmitted() throws Exception {
        // Arrange
        when(applicationService.submitApplication(any(ApplicationInputDTO.class)))
                .thenReturn(sampleApplication());

        // Act & Assert
        mockMvc.perform(post(BASE_URL + "/application")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(validInput())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.applicationId").value(1))
                .andExpect(jsonPath("$.status").value("SUBMITTED"))
                .andExpect(jsonPath("$.unitId").value(10));

        verify(applicationService, times(1)).submitApplication(any(ApplicationInputDTO.class));
    }

    @Test
    @DisplayName("POST application with malformed JSON -> 400 and service not invoked")
    void shouldReturn400WhenBodyIsMalformed() throws Exception {
        // Act & Assert
        mockMvc.perform(post(BASE_URL + "/application")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ not json"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(applicationService);
    }

    @Test
    @DisplayName("POST application for missing unit -> 404 Not Found")
    void shouldReturn404WhenUnitNotFound() throws Exception {
        // Arrange
        when(applicationService.submitApplication(any(ApplicationInputDTO.class)))
                .thenThrow(new UnitIdNotFoundException("UnitId not found"));

        // Act & Assert
        mockMvc.perform(post(BASE_URL + "/application")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(validInput())))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("UnitId not found")));

        verify(applicationService, times(1)).submitApplication(any(ApplicationInputDTO.class));
    }

    @Test
    @DisplayName("POST application for missing user -> 404 Not Found")
    void shouldReturn404WhenUserNotFound() throws Exception {
        // Arrange
        when(applicationService.submitApplication(any(ApplicationInputDTO.class)))
                .thenThrow(new UserIdNotFoundException("UserId not found"));

        // Act & Assert
        mockMvc.perform(post(BASE_URL + "/application")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(validInput())))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("UserId not found")));

        verify(applicationService, times(1)).submitApplication(any(ApplicationInputDTO.class));
    }

    @Test
    @DisplayName("GET applications by unit -> 200 with list")
    void shouldReturn200WhenApplicationsExist() throws Exception {
        // Arrange
        when(applicationService.getApplicationsByUnitId(10))
                .thenReturn(List.of(sampleApplication()));

        // Act & Assert
        mockMvc.perform(get(BASE_URL + "/unitId/{unitId}", 10))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].applicationId").value(1));

        verify(applicationService, times(1)).getApplicationsByUnitId(10);
    }

    @Test
    @DisplayName("GET applications by unit when none -> 200 with empty list")
    void shouldReturn200WithEmptyListWhenNoApplications() throws Exception {
        // Arrange
        when(applicationService.getApplicationsByUnitId(10)).thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get(BASE_URL + "/unitId/{unitId}", 10))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(applicationService, times(1)).getApplicationsByUnitId(10);
    }

    @Test
    @DisplayName("GET applications by unit with non-numeric id -> 400")
    void shouldReturn400WhenUnitIdIsNotNumeric() throws Exception {
        // Act & Assert
        mockMvc.perform(get(BASE_URL + "/unitId/{unitId}", "abc"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(applicationService);
    }

    @Test
    @DisplayName("PUT application status -> 200 with updated application")
    void shouldReturn200WhenStatusUpdated() throws Exception {
        // Arrange
        ApplicationOutputDTO approved = sampleApplication();
        approved.setStatus("APPROVED");
        when(applicationService.updateStatusOfApplication(eq(1), eq("APPROVED")))
                .thenReturn(approved);

        // Act & Assert
        mockMvc.perform(put(BASE_URL + "/{applicationId}/{status}", 1, "APPROVED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));

        verify(applicationService, times(1)).updateStatusOfApplication(eq(1), eq("APPROVED"));
    }

    @Test
    @DisplayName("PUT application status for missing application -> service failure surfaced")
    void shouldSurfaceServerErrorWhenApplicationNotFoundOnUpdate() {
        // Arrange
        when(applicationService.updateStatusOfApplication(eq(99), eq("APPROVED")))
                .thenThrow(new RuntimeException("Application not found"));

        // Act & Assert
        assertRequestFailsWith("Application not found",
                () -> mockMvc.perform(put(BASE_URL + "/{applicationId}/{status}", 99, "APPROVED")));

        verify(applicationService, times(1)).updateStatusOfApplication(eq(99), eq("APPROVED"));
    }
}
