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

import com.cts.dto.PropertyInputDTO;
import com.cts.dto.PropertyOutputDTO;
import com.cts.exception.UserIdNotFoundException;
import com.cts.serviceimpl.PropertyServiceImpl;

/**
 * Unit tests for {@link PropertyController}.
 *
 * <p>Mocks the concrete {@link PropertyServiceImpl} and injects it into the controller. Covers
 * bean-validation failures on {@code @Valid PropertyInputDTO}, the mapped 404
 * ({@link UserNotFoundException}) branch, and the required {@code pageno}/{@code pagesize} query
 * parameters which have no defaults and therefore yield 400 when omitted.
 */
@ExtendWith(MockitoExtension.class)
class PropertyControllerTest extends AbstractControllerTest {

    @Mock
    private PropertyServiceImpl propertyService;

    @InjectMocks
    private PropertyController propertyController;

    private MockMvc mockMvc;

    private static final String BASE_URL = "/api/v1/property";

    @BeforeEach
    void setUp() {
        mockMvc = buildMockMvc(propertyController);
    }

    private PropertyOutputDTO sampleProperty() {
        return PropertyOutputDTO.builder()
                .propertyId(1)
                .propertyName("Maple Residency")
                .propertyAddress("12 Park Lane")
                .propertyCity("Chennai")
                .propertyState("Tamil Nadu")
                .propertyPostalCode("600001")
                .propertyCountry("India")
                .status("AVAILABLE")
                .createdAt(LocalDate.now())
                .updatedAt(LocalDate.now())
                .ownerId(10)
                .build();
    }

    private PropertyInputDTO validInput() {
        return PropertyInputDTO.builder()
                .propertyName("Maple Residency")
                .propertyAddress("12 Park Lane")
                .propertyCity("Chennai")
                .propertyState("Tamil Nadu")
                .propertyCountry("India")
                .status("AVAILABLE")
                .propertyPostalCode("600001")
                .build();
    }

    @Test
    @DisplayName("POST property -> 201 with created property")
    void shouldReturn201WhenPropertyCreated() throws Exception {
        // Arrange
        when(propertyService.addProperty(any(PropertyInputDTO.class), eq(10)))
                .thenReturn(sampleProperty());

        // Act & Assert
        mockMvc.perform(post(BASE_URL + "/register/{ownerId}", 10)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(validInput())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.propertyId").value(1))
                .andExpect(jsonPath("$.name").value("Maple Residency"))
                .andExpect(jsonPath("$.ownerId").value(10));

        verify(propertyService, times(1)).addProperty(any(PropertyInputDTO.class), eq(10));
    }

    @Test
    @DisplayName("POST property with blank name -> 400 (validation) and service not invoked")
    void shouldReturn400WhenNameIsBlank() throws Exception {
        // Arrange
        PropertyInputDTO invalid = validInput();
        invalid.setPropertyName("");

        // Act & Assert
        mockMvc.perform(post(BASE_URL + "/register/{ownerId}", 10)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(invalid)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(propertyService);
    }

    @Test
    @DisplayName("POST property with out-of-range postal code -> 400 (validation)")
    void shouldReturn400WhenPostalCodeOutOfRange() throws Exception {
        // Arrange
        PropertyInputDTO invalid = validInput();
        invalid.setPropertyPostalCode("999");

        // Act & Assert
        mockMvc.perform(post(BASE_URL + "/register/{ownerId}", 10)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(invalid)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(propertyService);
    }

    @Test
    @DisplayName("POST property with malformed JSON -> 400 and service not invoked")
    void shouldReturn400WhenBodyIsMalformed() throws Exception {
        // Act & Assert
        mockMvc.perform(post(BASE_URL + "/register/{ownerId}", 10)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ broken json"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(propertyService);
    }

    @Test
    @DisplayName("POST property for missing owner -> 404 Not Found")
    void shouldReturn404WhenOwnerNotFound() throws Exception {
        // Arrange
        when(propertyService.addProperty(any(PropertyInputDTO.class), eq(99)))
                .thenThrow(new UserIdNotFoundException("Owner Id not found"));

        // Act & Assert
        mockMvc.perform(post(BASE_URL + "/register/{ownerId}", 99)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(validInput())))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Owner Id not found")));

        verify(propertyService, times(1)).addProperty(any(PropertyInputDTO.class), eq(99));
    }

    @Test
    @DisplayName("GET properties by city -> 200 with list")
    void shouldReturn200WhenPropertiesByCityExist() throws Exception {
        // Arrange
        when(propertyService.getPropertyByCity("Chennai"))
                .thenReturn(List.of(sampleProperty()));

        // Act & Assert
        mockMvc.perform(get(BASE_URL + "/city/{city}", "Chennai")
                        .param("pageno", "0")
                        .param("pagesize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].city").value("Chennai"));

        verify(propertyService, times(1)).getPropertyByCity("Chennai");
    }

    @Test
    @DisplayName("GET properties by city when none -> 200 with empty list")
    void shouldReturn200WithEmptyListWhenNoPropertiesByCity() throws Exception {
        // Arrange
        when(propertyService.getPropertyByCity("Nowhere"))
                .thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get(BASE_URL + "/city/{city}", "Nowhere")
                        .param("pageno", "0")
                        .param("pagesize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(propertyService, times(1)).getPropertyByCity("Nowhere");
    }

    @Test
    @DisplayName("GET properties by city without required paging params -> 400")
    void shouldReturn400WhenPagingParamsMissing() throws Exception {
        // Act & Assert
        mockMvc.perform(get(BASE_URL + "/city/{city}", "Chennai"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(propertyService);
    }

    @Test
    @DisplayName("GET properties by state -> 200 with list")
    void shouldReturn200WhenPropertiesByStateExist() throws Exception {
        // Arrange
        when(propertyService.getPropertyByState("Tamil Nadu"))
                .thenReturn(List.of(sampleProperty()));

        // Act & Assert
        mockMvc.perform(get(BASE_URL + "/state/{state}", "Tamil Nadu")
                        .param("pageno", "0")
                        .param("pagesize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].state").value("Tamil Nadu"));

        verify(propertyService, times(1)).getPropertyByState("Tamil Nadu");
    }

    @Test
    @DisplayName("GET properties by owner id -> 200 with list")
    void shouldReturn200WhenPropertiesByOwnerIdExist() throws Exception {
        // Arrange
        when(propertyService.getPropertyByOwnerId(10))
                .thenReturn(List.of(sampleProperty()));

        // Act & Assert
        mockMvc.perform(get(BASE_URL + "/ownerid/{ownerid}", 10)
                        .param("pageno", "0")
                        .param("pagesize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].ownerId").value(10));

        verify(propertyService, times(1)).getPropertyByOwnerId(10);
    }

    @Test
    @DisplayName("GET properties by owner id with non-numeric id -> 400 and service not invoked")
    void shouldReturn400WhenOwnerIdNotNumeric() throws Exception {
        // Act & Assert
        mockMvc.perform(get(BASE_URL + "/ownerid/{ownerid}", "abc")
                        .param("pageno", "0")
                        .param("pagesize", "10"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(propertyService);
    }
}
