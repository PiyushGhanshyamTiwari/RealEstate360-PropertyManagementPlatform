package com.cts.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import com.cts.dto.TenantProfileInputDTO;
import com.cts.dto.TenantProfileOutputDTO;
import com.cts.service.TenantProfileService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class TenantProfileControllerTest {

    @Mock
    private TenantProfileService service;

    @InjectMocks
    private TenantProfileController controller;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    
    @Test
    void testAddTenant() throws Exception {

        TenantProfileInputDTO input = new TenantProfileInputDTO();
        input.setUserId(1);
        input.setDocumentType("ID");

        when(service.addTenant(any()))
                .thenReturn(new TenantProfileOutputDTO());

        mockMvc.perform(multipart("/api/v1/tenant/register")
                        .file(new MockMultipartFile(
                                "file", "doc.pdf", "application/pdf", "data".getBytes()))
                        .param("userId", "1")
                        .param("documentType", "ID")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk());

        verify(service).addTenant(any());
    }

   
    @Test
    void testGetAllTenants() throws Exception {

        when(service.getAllTenants())
                .thenReturn(List.of(new TenantProfileOutputDTO()));

        mockMvc.perform(get("/api/v1/tenant/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        verify(service).getAllTenants();
    }

    
    @Test
    void testGetAllTenantsEmpty() throws Exception {

        when(service.getAllTenants())
                .thenReturn(List.of());

        mockMvc.perform(get("/api/v1/tenant/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    
    @Test
    void testGetTenantById() throws Exception {

        when(service.getTenantById(1))
                .thenReturn(new TenantProfileOutputDTO());

        mockMvc.perform(get("/api/v1/tenant/tenantId/{tenantId}", 1))
                .andExpect(status().isOk());

        verify(service).getTenantById(1);
    }

    
    @Test
    void testGetTenantByUserId() throws Exception {

        when(service.getTenantByUserId(1))
                .thenReturn(new TenantProfileOutputDTO());

        mockMvc.perform(get("/api/v1/tenant/userId/{userId}", 1))
                .andExpect(status().isOk());

        verify(service).getTenantByUserId(1);
    }
}