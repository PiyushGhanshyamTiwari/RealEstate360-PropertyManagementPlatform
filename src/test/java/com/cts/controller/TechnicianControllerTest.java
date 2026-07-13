package com.cts.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import com.cts.dto.*;
import com.cts.service.TechnicianService;
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
class TechnicianControllerTest {

    @Mock
    private TechnicianService technicianService;

    @InjectMocks
    private TechnicianController controller;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

   
    @Test
    void testCreateTechnician() throws Exception {

        TechnicianInputDTO input = new TechnicianInputDTO();
        input.setUserId(10);
        input.setSpecialization("ELECTRICIAN");
        input.setCity("Chennai");

        when(technicianService.createTechnician(any()))
                .thenReturn(new TechnicianOutputDTO());

        mockMvc.perform(post("/technicians")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isCreated());

        verify(technicianService).createTechnician(any());
    }

    
    @Test
    void testGetTechnicianById() throws Exception {

        when(technicianService.getTechnicianById(1))
                .thenReturn(new TechnicianOutputDTO());

        mockMvc.perform(get("/technicians/{userid}", 1))
                .andExpect(status().isOk());

        verify(technicianService).getTechnicianById(1);
    }

    
    @Test
    void testGetAllTechnicians() throws Exception {

        when(technicianService.getAllTechnicians())
                .thenReturn(List.of(new TechnicianOutputDTO()));

        mockMvc.perform(get("/technicians"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        verify(technicianService).getAllTechnicians();
    }

    
    @Test
    void testGetAllTechniciansEmpty() throws Exception {

        when(technicianService.getAllTechnicians())
                .thenReturn(List.of());

        mockMvc.perform(get("/technicians"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

   
    @Test
    void testSearchTechnicians() throws Exception {

        when(technicianService.getTechnicianBySpecializationAndCity("electrician", "Chennai"))
                .thenReturn(List.of(new TechnicianOutputDTO()));

        mockMvc.perform(get("/technicians/search")
                        .param("specialization", "electrician")
                        .param("city", "Chennai"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        verify(technicianService)
                .getTechnicianBySpecializationAndCity("electrician", "Chennai");
    }

    
    @Test
    void testSearchTechniciansEmpty() throws Exception {

        when(technicianService.getTechnicianBySpecializationAndCity("electrician", "Chennai"))
                .thenReturn(List.of());

        mockMvc.perform(get("/technicians/search")
                        .param("specialization", "electrician")
                        .param("city", "Chennai"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

   
    @Test
    void testGetWorkHistoryById() throws Exception {

        when(technicianService.getWorkHistory(1))
                .thenReturn(List.of(new MaintenanceScheduleResponseDTO()));

        mockMvc.perform(get("/technicians/{userid}/getAllSchedules", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        verify(technicianService).getWorkHistory(1);
    }

    
    @Test
    void testGetWorkHistoryByIdEmpty() throws Exception {

        when(technicianService.getWorkHistory(1))
                .thenReturn(List.of());

        mockMvc.perform(get("/technicians/{userid}/getAllSchedules", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}