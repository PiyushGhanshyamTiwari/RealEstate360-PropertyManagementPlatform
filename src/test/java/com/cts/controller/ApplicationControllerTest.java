package com.cts.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import com.cts.dto.ApplicationInputDTO;
import com.cts.dto.ApplicationOutputDTO;
import com.cts.service.ApplicationService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class ApplicationControllerTest {

    @Mock
    private ApplicationService applicationService;

    @InjectMocks
    private ApplicationController controller;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    
    @Test
    void testSubmitApplication() throws Exception {

        ApplicationInputDTO input = new ApplicationInputDTO();
        input.setUserId(10);
        input.setUnitId(1);

        when(applicationService.submitApplication(any()))
                .thenReturn(new ApplicationOutputDTO());

        mockMvc.perform(post("/api/v1/application/application")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isCreated());

        verify(applicationService).submitApplication(any());
    }

    
    @Test
    void testGetApplicationsByUnitId() throws Exception {

        when(applicationService.getApplicationsByUnitId(1))
                .thenReturn(List.of(new ApplicationOutputDTO()));

        mockMvc.perform(get("/api/v1/application/unitId/{unitId}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        verify(applicationService).getApplicationsByUnitId(1);
    }

    
    @Test
    void testGetApplicationsByUnitIdEmpty() throws Exception {

        when(applicationService.getApplicationsByUnitId(1))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/v1/application/unitId/{unitId}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    
    @Test
    void testUpdateStatusOfApplication() throws Exception {

        when(applicationService.updateStatusOfApplication(1, "Approved"))
                .thenReturn(new ApplicationOutputDTO());

        mockMvc.perform(put("/api/v1/application/{applicationId}/{status}", 1, "Approved"))
                .andExpect(status().isOk());

        verify(applicationService).updateStatusOfApplication(1, "Approved");
    }

    
    @Test
    void testGetApplicationByTenantId() throws Exception {

        when(applicationService.getApplicationByTenantId(10))
                .thenReturn(List.of(new ApplicationOutputDTO()));

        mockMvc.perform(get("/api/v1/application/userId/{userId}", 10))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        verify(applicationService).getApplicationByTenantId(10);
    }

    
    @Test
    void testGetApplicationByTenantIdEmpty() throws Exception {

        when(applicationService.getApplicationByTenantId(10))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/v1/application/userId/{userId}", 10))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    
    @Test
    void testGetApplicationByApplicationId() throws Exception {

        when(applicationService.getApplicationByApplicationId(1))
                .thenReturn(new ApplicationOutputDTO());

        mockMvc.perform(get("/api/v1/application/applicationId/{applicationId}", 1))
                .andExpect(status().isOk());

        verify(applicationService).getApplicationByApplicationId(1);
    }
}
