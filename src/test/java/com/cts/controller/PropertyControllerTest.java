package com.cts.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import com.cts.dto.PropertyInputDTO;
import com.cts.dto.PropertyOutputDTO;
import com.cts.service.PropertyService;
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
class PropertyControllerTest {

    @Mock
    private PropertyService propertyService;

    @InjectMocks
    private PropertyController controller;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

   
    @Test
    void testAddProperty() throws Exception {

        PropertyInputDTO input = new PropertyInputDTO();

        when(propertyService.addProperty(any(), eq(1)))
                .thenReturn(new PropertyOutputDTO());

        mockMvc.perform(post("/api/v1/property/register/{ownerId}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isCreated());
    }

   
    @Test
    void testFindPropertyByCity() throws Exception {

        when(propertyService.findPropertyByCity("Chennai"))
                .thenReturn(List.of(new PropertyOutputDTO()));

        mockMvc.perform(get("/api/v1/property/city/{city}", "Chennai"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void testFindPropertyByCityEmpty() throws Exception {

        when(propertyService.findPropertyByCity("Chennai"))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/v1/property/city/{city}", "Chennai"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }


    @Test
    void testFindPropertyByState() throws Exception {

        when(propertyService.findPropertyByState("TN"))
                .thenReturn(List.of(new PropertyOutputDTO()));

        mockMvc.perform(get("/api/v1/property/state/{state}", "TN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void testFindPropertyByStateEmpty() throws Exception {

        when(propertyService.findPropertyByState("TN"))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/v1/property/state/{state}", "TN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

   
    @Test
    void testFindPropertyByOwnerId() throws Exception {

        when(propertyService.findPropertyByOwnerId(1))
                .thenReturn(List.of(new PropertyOutputDTO()));

        mockMvc.perform(get("/api/v1/property/ownerId/{ownerId}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    
    @Test
    void testFindPropertyByOwnerIdEmpty() throws Exception {

        when(propertyService.findPropertyByOwnerId(1))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/v1/property/ownerId/{ownerId}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}
