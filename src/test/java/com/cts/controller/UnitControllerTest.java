package com.cts.controller;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
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

import com.cts.dto.UnitInputDTO;
import com.cts.dto.UnitOutputDTO;
import com.cts.exception.PropertyIdNotFoundException;
import com.cts.serviceimpl.UnitServiceImpl;

/**
 * Unit tests for {@link UnitController}.
 *
 * <p>Mocks the concrete {@link UnitServiceImpl} and injects it into the controller. Covers
 * bean-validation on {@code @Valid UnitInputDTO}, the mapped 404 ({@link PropertyNotFoundException})
 * branch, every finder endpoint with populated and empty results, and 400s from non-numeric typed
 * path variables ({@code areaSqFt}/{@code floor}).
 */
@ExtendWith(MockitoExtension.class)
class UnitControllerTest extends AbstractControllerTest {

    @Mock
    private UnitServiceImpl unitService;

    @InjectMocks
    private UnitController unitController;

    private MockMvc mockMvc;

    private static final String BASE_URL = "/api/v1/unit";

    @BeforeEach
    void setUp() {
        mockMvc = buildMockMvc(unitController);
    }

    private UnitOutputDTO sampleUnit() {
        return UnitOutputDTO.builder()
                .unitId(1)
                .type("2BHK")
                .areaSqFt(1200.0)
                .floor(2)
                .rentAmount(15000.0)
                .depositAmount(30000.0)
                .availableFrom(LocalDate.now())
                .createdAt(LocalDate.now())
                .updatedAt(LocalDate.now())
                .propertyId(1)
                .build();
    }

    private UnitInputDTO validInput() {
        return UnitInputDTO.builder()
                .type("2BHK")
                .areaSqFt(1200.0)
                .floor(2)
                .rentAmount(15000.0)
                .depositAmount(30000.0)
                .availableFrom(LocalDate.now())
                .propertyId(1)
                .build();
    }

    @Test
    @DisplayName("POST unit -> 201 with created unit")
    void shouldReturn201WhenUnitCreated() throws Exception {
        // Arrange
        when(unitService.addUnit(any(UnitInputDTO.class))).thenReturn(sampleUnit());

        // Act & Assert
        mockMvc.perform(post(BASE_URL + "/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(validInput())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.unitId").value(1))
                .andExpect(jsonPath("$.type").value("2BHK"));

        verify(unitService, times(1)).addUnit(any(UnitInputDTO.class));
    }

    @Test
    @DisplayName("POST unit with blank type -> 400 (validation) and service not invoked")
    void shouldReturn400WhenTypeIsBlank() throws Exception {
        // Arrange
        UnitInputDTO invalid = validInput();
        invalid.setType("");

        // Act & Assert
        mockMvc.perform(post(BASE_URL + "/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(invalid)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(unitService);
    }

    @Test
    @DisplayName("POST unit with non-positive rent -> 400 (validation) and service not invoked")
    void shouldReturn400WhenRentNotPositive() throws Exception {
        // Arrange
        UnitInputDTO invalid = validInput();
        invalid.setRentAmount(0.0);

        // Act & Assert
        mockMvc.perform(post(BASE_URL + "/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(invalid)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(unitService);
    }

    @Test
    @DisplayName("POST unit with malformed JSON -> 400 and service not invoked")
    void shouldReturn400WhenBodyIsMalformed() throws Exception {
        // Act & Assert
        mockMvc.perform(post(BASE_URL + "/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ broken json"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(unitService);
    }

    @Test
    @DisplayName("POST unit for missing property -> 404 Not Found")
    void shouldReturn404WhenPropertyNotFound() throws Exception {
        // Arrange
        when(unitService.addUnit(any(UnitInputDTO.class)))
                .thenThrow(new PropertyIdNotFoundException("Property Id not found"));

        // Act & Assert
        mockMvc.perform(post(BASE_URL + "/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(validInput())))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Property Id not found")));

        verify(unitService, times(1)).addUnit(any(UnitInputDTO.class));
    }

    @Test
    @DisplayName("GET all units -> 200 with list")
    void shouldReturn200WhenUnitsExist() throws Exception {
        // Arrange
        when(unitService.getAllUnit()).thenReturn(List.of(sampleUnit()));

        // Act & Assert
        mockMvc.perform(get(BASE_URL + "/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].unitId").value(1));

        verify(unitService, times(1)).getAllUnit();
    }

    @Test
    @DisplayName("GET all units when none -> 200 with empty list")
    void shouldReturn200WithEmptyListWhenNoUnits() throws Exception {
        // Arrange
        when(unitService.getAllUnit()).thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get(BASE_URL + "/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(unitService, times(1)).getAllUnit();
    }

    @Test
    @DisplayName("GET units by type -> 200 with list")
    void shouldReturn200WhenUnitsByTypeExist() throws Exception {
        // Arrange
        when(unitService.getUnitByType("2BHK")).thenReturn(List.of(sampleUnit()));

        // Act & Assert
        mockMvc.perform(get(BASE_URL + "/type/{type}", "2BHK"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].type").value("2BHK"));

        verify(unitService, times(1)).getUnitByType("2BHK");
    }

    @Test
    @DisplayName("GET units by area -> 200 with list")
    void shouldReturn200WhenUnitsByAreaExist() throws Exception {
        // Arrange
        when(unitService.getUnitByAreaSqFt(1200.0)).thenReturn(List.of(sampleUnit()));

        // Act & Assert
        mockMvc.perform(get(BASE_URL + "/areaSqFt/{areaSqFt}", "1200.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].areaSqFt").value(1200.0));

        verify(unitService, times(1)).getUnitByAreaSqFt(1200.0);
    }

    @Test
    @DisplayName("GET units by area with non-numeric value -> 400 and service not invoked")
    void shouldReturn400WhenAreaNotNumeric() throws Exception {
        // Act & Assert
        mockMvc.perform(get(BASE_URL + "/areaSqFt/{areaSqFt}", "abc"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(unitService);
    }

    @Test
    @DisplayName("GET units by floor -> 200 with list")
    void shouldReturn200WhenUnitsByFloorExist() throws Exception {
        // Arrange
        when(unitService.getUnitByFloor(2)).thenReturn(List.of(sampleUnit()));

        // Act & Assert
        mockMvc.perform(get(BASE_URL + "/floor/{floor}", 2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].floor").value(2));

        verify(unitService, times(1)).getUnitByFloor(2);
    }

    @Test
    @DisplayName("GET units by floor with non-numeric value -> 400 and service not invoked")
    void shouldReturn400WhenFloorNotNumeric() throws Exception {
        // Act & Assert
        mockMvc.perform(get(BASE_URL + "/floor/{floor}", "abc"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(unitService);
    }

    @Test
    @DisplayName("GET units by rent range -> 200 with list")
    void shouldReturn200WhenUnitsByRentRangeExist() throws Exception {
        // Arrange
        when(unitService.findUnitByRentAmountBetween(10000.0, 20000.0)).thenReturn(List.of(sampleUnit()));

        // Act & Assert
        mockMvc.perform(get(BASE_URL + "/rentAmount/{min}/{max}", "10000.0", "20000.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].rentAmount").value(15000.0));

        verify(unitService, times(1)).findUnitByRentAmountBetween(10000.0, 20000.0);
    }

    @Test
    @DisplayName("GET units by property id -> 200 with list")
    void shouldReturn200WhenUnitsByPropertyIdExist() throws Exception {
        // Arrange
        when(unitService.findUnitByPropertyId(1)).thenReturn(List.of(sampleUnit()));

        // Act & Assert
        mockMvc.perform(get(BASE_URL + "/propertyId/{propertyId}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].propertyId").value(1));

        verify(unitService, times(1)).findUnitByPropertyId(1);
    }

    @Test
    @DisplayName("GET units by property id when none -> 200 with empty list")
    void shouldReturn200WithEmptyListWhenNoUnitsByPropertyId() throws Exception {
        // Arrange
        when(unitService.findUnitByPropertyId(99)).thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get(BASE_URL + "/propertyId/{propertyId}", 99))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(unitService, times(1)).findUnitByPropertyId(99);
    }
}
