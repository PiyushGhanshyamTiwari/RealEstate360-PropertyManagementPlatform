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

import java.time.LocalDate;
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

import com.cts.dto.AmenityInputDTO;
import com.cts.dto.AmenityOutputDTO;
import com.cts.exception.UnitIdNotFoundException;
import com.cts.serviceimpl.AmenityServiceImpl;

@ExtendWith(MockitoExtension.class)
class AmenityControllerTest extends AbstractControllerTest {

    @Mock
    private AmenityServiceImpl amenityService;

    @InjectMocks
    private AmenityController amenityController;

    private MockMvc mockMvc;

    private static final String BASE_URL = "/api/v1/amenity";

    @BeforeEach
    void setUp() {
        mockMvc = buildMockMvc(amenityController);
    }

    private AmenityOutputDTO sampleAmenity() {
        return AmenityOutputDTO.builder()
                .amenityId(1)
                .unitId(10)
                .name("Swimming Pool")
                .description("Rooftop pool")
                .createdAt(LocalDate.now())
                .build();
    }

    private AmenityInputDTO validInput() {
        return AmenityInputDTO.builder()
                .name("Swimming Pool")
                .description("Rooftop pool")
                .build();
    }

    @Test
    @DisplayName("POST amenity -> 201 with created amenity")
    void shouldReturn201WhenAmenityCreated() throws Exception {
        // Arrange
        when(amenityService.addAmenity(any(AmenityInputDTO.class), eq(10)))
                .thenReturn(sampleAmenity());

        // Act & Assert
        mockMvc.perform(post(BASE_URL + "/register/{unitId}", 10)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(validInput())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.amenityId").value(1))
                .andExpect(jsonPath("$.name").value("Swimming Pool"))
                .andExpect(jsonPath("$.unitId").value(10));

        verify(amenityService, times(1)).addAmenity(any(AmenityInputDTO.class), eq(10));
    }

    @Test
    @DisplayName("POST amenity with blank name -> 400 (validation) and service not invoked")
    void shouldReturn400WhenAmenityNameIsBlank() throws Exception {
        // Arrange
        AmenityInputDTO invalid = AmenityInputDTO.builder().name("").description("desc").build();

        // Act & Assert
        mockMvc.perform(post(BASE_URL + "/register/{unitId}", 10)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(invalid)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(amenityService);
    }

    @Test
    @DisplayName("POST amenity with malformed JSON -> 400 and service not invoked")
    void shouldReturn400WhenBodyIsMalformed() throws Exception {
        // Act & Assert
        mockMvc.perform(post(BASE_URL + "/register/{unitId}", 10)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ broken json"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(amenityService);
    }

    @Test
    @DisplayName("POST amenity for missing unit -> 404 Not Found")
    void shouldReturn404WhenUnitNotFound() throws Exception {
        // Arrange
        when(amenityService.addAmenity(any(AmenityInputDTO.class), eq(99)))
                .thenThrow(new UnitIdNotFoundException("Unit Id not found"));

        // Act & Assert
        mockMvc.perform(post(BASE_URL + "/register/{unitId}", 99)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(validInput())))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Unit Id not found")));

        verify(amenityService, times(1)).addAmenity(any(AmenityInputDTO.class), eq(99));
    }

    @Test
    @DisplayName("GET all amenities -> 200 with populated list")
    void shouldReturn200WhenAmenitiesExist() throws Exception {
        // Arrange
        when(amenityService.getAllAmenities()).thenReturn(List.of(sampleAmenity()));

        // Act & Assert
        mockMvc.perform(get(BASE_URL + "/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Swimming Pool"));

        verify(amenityService, times(1)).getAllAmenities();
    }

    @Test
    @DisplayName("GET all amenities when none -> 200 with empty list")
    void shouldReturn200WithEmptyListWhenNoAmenities() throws Exception {
        // Arrange
        when(amenityService.getAllAmenities()).thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get(BASE_URL + "/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(amenityService, times(1)).getAllAmenities();
    }

    @Test
    @DisplayName("GET amenities by unit -> 200 with list")
    void shouldReturn200WhenAmenitiesByUnitExist() throws Exception {
        // Arrange
        when(amenityService.getAmenitiesByUnit(10)).thenReturn(List.of(sampleAmenity()));

        // Act & Assert
        mockMvc.perform(get(BASE_URL + "/unit/{unitId}", 10))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].unitId").value(10));

        verify(amenityService, times(1)).getAmenitiesByUnit(10);
    }

    @Test
    @DisplayName("GET amenities by name -> 200 with list")
    void shouldReturn200WhenAmenitiesByNameExist() throws Exception {
        // Arrange
        when(amenityService.getAmenitiesByName("Swimming Pool"))
                .thenReturn(List.of(sampleAmenity()));

        // Act & Assert
        mockMvc.perform(get(BASE_URL + "/name/{name}", "Swimming Pool"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Swimming Pool"));

        verify(amenityService, times(1)).getAmenitiesByName("Swimming Pool");
    }

    @Test
    @DisplayName("GET amenities by name when none -> 200 with empty list")
    void shouldReturn200WithEmptyListWhenNoAmenitiesByName() throws Exception {
        // Arrange
        when(amenityService.getAmenitiesByName("Unknown")).thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get(BASE_URL + "/name/{name}", "Unknown"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(amenityService, times(1)).getAmenitiesByName("Unknown");
    }
}
