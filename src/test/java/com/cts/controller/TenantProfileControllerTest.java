package com.cts.controller;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import com.cts.dto.TenantProfileInputDTO;
import com.cts.dto.TenantProfileOutputDTO;
import com.cts.exception.TenantIdNotFoundException;
import com.cts.exception.UserIdNotFoundException;
import com.cts.serviceimpl.TenantProfileServiceImpl;

/**
 * Unit tests for {@link TenantProfileController}.
 *
 * <p>Mocks the concrete {@link TenantProfileServiceImpl} and injects it into the controller. The
 * register endpoint consumes {@code multipart/form-data} and binds the {@link TenantProfileInputDto}
 * as a model attribute (the controller uses the Swagger {@code @RequestBody}, not Spring's), so the
 * request is built with form params plus a file part named {@code documentFileRef}.
 */
@ExtendWith(MockitoExtension.class)
class TenantProfileControllerTest extends AbstractControllerTest {

    @Mock
    private TenantProfileServiceImpl tenantProfileService;

    @InjectMocks
    private TenantProfileController tenantProfileController;

    private MockMvc mockMvc;

    private static final String BASE_URL = "/api/v1/tenant";

    @BeforeEach
    void setUp() {
        mockMvc = buildMockMvc(tenantProfileController);
    }

    private TenantProfileOutputDTO sampleTenant() {
        return TenantProfileOutputDTO.builder()
                .tenantId(1)
                .address("12 Park Lane")
                .createdAt(LocalDateTime.now())
                .documentType("AADHAAR")
                .documentFileRef("uploads/doc.pdf")
                .userName("Jane Doe")
                .phone(9876543210L)
                .emailId("jane@example.com")
                .build();
    }

    private MockMultipartFile documentPart() {
        return new MockMultipartFile(
                "documentFileRef", "doc.pdf", MediaType.APPLICATION_PDF_VALUE, "doc-bytes".getBytes());
    }

    @Test
    @DisplayName("POST register tenant -> 200 with created tenant")
    void shouldReturn200WhenTenantRegistered() throws Exception {
        // Arrange
        when(tenantProfileService.addTenant(any(TenantProfileInputDTO.class)))
                .thenReturn(sampleTenant());

        // Act & Assert
        mockMvc.perform(multipart(BASE_URL + "/register")
                        .file(documentPart())
                        .param("userId", "5")
                        .param("address", "12 Park Lane")
                        .param("documentType", "AADHAAR"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tenantId").value(1))
                .andExpect(jsonPath("$.fullName").value("Jane Doe"));

        verify(tenantProfileService, times(1)).addTenant(any(TenantProfileInputDTO.class));
    }

    @Test
    @DisplayName("POST register tenant when service fails -> failure surfaced")
    void shouldSurfaceFailureWhenRegistrationFails() throws Exception {
        // Arrange
        when(tenantProfileService.addTenant(any(TenantProfileInputDTO.class)))
                .thenThrow(new RuntimeException("User not found"));

        // Act & Assert
        assertRequestFailsWith("User not found", () ->
                mockMvc.perform(multipart(BASE_URL + "/register")
                        .file(documentPart())
                        .param("userId", "5")
                        .param("address", "12 Park Lane")
                        .param("documentType", "AADHAAR")));

        verify(tenantProfileService, times(1)).addTenant(any(TenantProfileInputDTO.class));
    }

    @Test
    @DisplayName("GET all tenants -> 200 with list")
    void shouldReturn200WhenTenantsExist() throws Exception {
        // Arrange
        when(tenantProfileService.getAllTenants()).thenReturn(List.of(sampleTenant()));

        // Act & Assert
        mockMvc.perform(get(BASE_URL + "/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].tenantId").value(1));

        verify(tenantProfileService, times(1)).getAllTenants();
    }

    @Test
    @DisplayName("GET all tenants when none -> 200 with empty list")
    void shouldReturn200WithEmptyListWhenNoTenants() throws Exception {
        // Arrange
        when(tenantProfileService.getAllTenants()).thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get(BASE_URL + "/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(tenantProfileService, times(1)).getAllTenants();
    }

    @Test
    @DisplayName("GET tenant by id -> 200 with tenant")
    void shouldReturn200WhenTenantExists() throws Exception {
        // Arrange
        when(tenantProfileService.getTenantById(1)).thenReturn(sampleTenant());

        // Act & Assert
        mockMvc.perform(get(BASE_URL + "/tenantId/{tenantId}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tenantId").value(1))
                .andExpect(jsonPath("$.emailId").value("jane@example.com"));

        verify(tenantProfileService, times(1)).getTenantById(1);
    }

    @Test
    @DisplayName("GET tenant by id for missing tenant -> 404 Not Found")
    void shouldReturn404WhenTenantNotFound() throws Exception {
        // Arrange
        when(tenantProfileService.getTenantById(99))
                .thenThrow(new TenantIdNotFoundException("Tenant Id not found"));

        // Act & Assert
        mockMvc.perform(get(BASE_URL + "/tenantId/{tenantId}", 99))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Tenant Id not found")));

        verify(tenantProfileService, times(1)).getTenantById(99);
    }

    @Test
    @DisplayName("GET tenant by id with non-numeric id -> 400 and service not invoked")
    void shouldReturn400WhenTenantIdNotNumeric() throws Exception {
        // Act & Assert
        mockMvc.perform(get(BASE_URL + "/tenantId/{tenantId}", "abc"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(tenantProfileService);
    }

    @Test
    @DisplayName("GET tenant by user id -> 200 with tenant")
    void shouldReturn200WhenTenantByUserIdExists() throws Exception {
        // Arrange
        when(tenantProfileService.getTenantByUserId(5)).thenReturn(sampleTenant());

        // Act & Assert
        mockMvc.perform(get(BASE_URL + "/userId/{userId}", 5))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tenantId").value(1));

        verify(tenantProfileService, times(1)).getTenantByUserId(5);
    }

    @Test
    @DisplayName("GET tenant by user id for missing user -> 404 Not Found")
    void shouldReturn404WhenUserNotFound() throws Exception {
        // Arrange
        when(tenantProfileService.getTenantByUserId(99))
                .thenThrow(new UserIdNotFoundException("User not found"));

        // Act & Assert
        mockMvc.perform(get(BASE_URL + "/userId/{userId}", 99))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("User not found")));

        verify(tenantProfileService, times(1)).getTenantByUserId(99);
    }
}
