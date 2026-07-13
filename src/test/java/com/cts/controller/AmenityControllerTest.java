package com.cts.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import com.cts.dto.AmenityInputDTO;
import com.cts.dto.AmenityOutputDTO;
import com.cts.service.AmenityService;
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
class AmenityControllerTest {

    @Mock
    private AmenityService amenityService;

    @InjectMocks
    private AmenityController controller;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    
    @Test
    void testAddAmenity() throws Exception {

        AmenityInputDTO input = new AmenityInputDTO();
        input.setName("Gym");
        input.setDescription("Fitness");

        when(amenityService.addAmenity(any(), eq(1)))
                .thenReturn(new AmenityOutputDTO());

        mockMvc.perform(post("/api/v1/amenity/register/{unitId}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isCreated());

        verify(amenityService).addAmenity(any(), eq(1));
    }

    @Test
    void testGetAllAmenities() throws Exception {

        when(amenityService.getAllAmenities())
                .thenReturn(List.of(new AmenityOutputDTO()));

        mockMvc.perform(get("/api/v1/amenity/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        verify(amenityService).getAllAmenities();
    }

    
    @Test
    void testGetAllAmenitiesEmpty() throws Exception {

        when(amenityService.getAllAmenities())
                .thenReturn(List.of());

        mockMvc.perform(get("/api/v1/amenity/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    
    @Test
    void testGetAmenitiesByUnit() throws Exception {

        when(amenityService.getAmenitiesByUnit(1))
                .thenReturn(List.of(new AmenityOutputDTO()));

        mockMvc.perform(get("/api/v1/amenity/unit/{unitId}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        verify(amenityService).getAmenitiesByUnit(1);
    }

    
    @Test
    void testGetAmenitiesByUnitEmpty() throws Exception {

        when(amenityService.getAmenitiesByUnit(1))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/v1/amenity/unit/{unitId}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

   
    @Test
    void testGetAmenitiesByName() throws Exception {

        when(amenityService.getAmenitiesByName("Gym"))
                .thenReturn(List.of(new AmenityOutputDTO()));

        mockMvc.perform(get("/api/v1/amenity/name/{name}", "Gym"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        verify(amenityService).getAmenitiesByName("Gym");
    }

    
    @Test
    void testGetAmenitiesByNameEmpty() throws Exception {

        when(amenityService.getAmenitiesByName("Gym"))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/v1/amenity/name/{name}", "Gym"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}
