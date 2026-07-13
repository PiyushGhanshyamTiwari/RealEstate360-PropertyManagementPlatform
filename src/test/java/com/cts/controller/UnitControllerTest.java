package com.cts.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import com.cts.dto.UnitInputDTO;
import com.cts.dto.UnitOutputDTO;
import com.cts.service.UnitService;
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
class UnitControllerTest {

    @Mock
    private UnitService unitService;

    @InjectMocks
    private UnitController controller;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    
    @Test
    void testAddUnit() throws Exception {
        UnitInputDTO input = new UnitInputDTO();

        when(unitService.addUnit(any()))
                .thenReturn(new UnitOutputDTO());

        mockMvc.perform(post("/api/v1/unit/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isCreated());

        verify(unitService).addUnit(any());
    }

  
    @Test
    void testGetAllUnit() throws Exception {

        when(unitService.findAllUnit())
                .thenReturn(List.of(new UnitOutputDTO()));

        mockMvc.perform(get("/api/v1/unit/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        verify(unitService).findAllUnit();
    }

    
    @Test
    void testGetAllUnitEmpty() throws Exception {

        when(unitService.findAllUnit())
                .thenReturn(List.of());

        mockMvc.perform(get("/api/v1/unit/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    
    @Test
    void testGetUnitByType() throws Exception {

        when(unitService.findUnitByType("2BHK"))
                .thenReturn(List.of(new UnitOutputDTO()));

        mockMvc.perform(get("/api/v1/unit/type/{type}", "2BHK"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        verify(unitService).findUnitByType("2BHK");
    }

  
    @Test
    void testGetUnitByAreaSqFt() throws Exception {

        when(unitService.findUnitByAreaSqFt(1200))
                .thenReturn(List.of(new UnitOutputDTO()));

        mockMvc.perform(get("/api/v1/unit/areaSqFt/{area}", 1200))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        verify(unitService).findUnitByAreaSqFt(1200);
    }

   
    @Test
    void testGetUnitByFloor() throws Exception {

        when(unitService.findUnitByFloor(2))
                .thenReturn(List.of(new UnitOutputDTO()));

        mockMvc.perform(get("/api/v1/unit/floor/{floor}", 2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        verify(unitService).findUnitByFloor(2);
    }

    
    @Test
    void testFindUnitByRentRange() throws Exception {

        when(unitService.findUnitByPriceRange(10000, 20000))
                .thenReturn(List.of(new UnitOutputDTO()));

        mockMvc.perform(get("/api/v1/unit/rentAmount/{min}/{max}", 10000, 20000))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        verify(unitService).findUnitByPriceRange(10000, 20000);
    }

    
    @Test
    void testFindUnitByPropertyId() throws Exception {

        when(unitService.findUnitByPropertyId(1))
                .thenReturn(List.of(new UnitOutputDTO()));

        mockMvc.perform(get("/api/v1/unit/propertyId/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        verify(unitService).findUnitByPropertyId(1);
    }

    @Test
    void testGetUnitByStatus() throws Exception {

        when(unitService.findUnitByStatus("AVAILABLE"))
                .thenReturn(List.of(new UnitOutputDTO()));

        mockMvc.perform(get("/api/v1/unit/status/{status}", "AVAILABLE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        verify(unitService).findUnitByStatus("AVAILABLE");
    }

    
    @Test
    void testGetUnitByTypeEmpty() throws Exception {

        when(unitService.findUnitByType("2BHK"))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/v1/unit/type/{type}", "2BHK"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}