package com.cts.controller;
 
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
 
import java.time.LocalDate;
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
import org.springframework.test.web.servlet.MockMvc;
 
import com.cts.dto.TechnicianInputDTO;
import com.cts.dto.TechnicianOutputDTO;
import com.cts.exception.TechnicianNotFoundException;
import com.cts.exception.UserIdNotFoundException;
import com.cts.serviceimpl.TechnicianServiceImpl;
 
/**
* Unit tests for {@link TechnicianController}.
*
* <p>Mocks the concrete {@link TechnicianServiceImpl} and injects it into the controller. Covers
* creation/update validation, the mapped 404 branches, the {@code void} deactivate path (stubbed
* with {@code doThrow}/{@code doNothing}), and list/paged retrieval including empty responses.
*/
@ExtendWith(MockitoExtension.class)
class TechnicianControllerTest extends AbstractControllerTest {
 
    @Mock
    private TechnicianServiceImpl technicianService;
 
    @InjectMocks
    private TechnicianController technicianController;
 
    private MockMvc mockMvc;
 
    private static final String BASE_URL = "/technicians";
 
    @BeforeEach
    void setUp() {
        mockMvc = buildMockMvc(technicianController);
    }
 
    private TechnicianOutputDTO sampleTechnician() {
        return TechnicianOutputDTO.builder()
                .technicianId(1)
                .userId(5)
                .specialization("PLUMBING")
                .status("ACTIVE")
                .available(true)
                .hireDate(LocalDate.now())
                .city("Chennai")
                .build();
    }
 
    private TechnicianInputDTO validInput() {
        TechnicianInputDTO dto = new TechnicianInputDTO();
        dto.setUserId(5);
        dto.setSpecialization("PLUMBING");
        dto.setCity("Chennai");
        dto.setHireDate(LocalDate.now());
        return dto;
    }
 
    @Test
    @DisplayName("POST technician -> 201 with created technician")
    void shouldReturn201WhenTechnicianCreated() throws Exception {
        // Arrange
        when(technicianService.createTechnician(any(TechnicianInputDTO.class)))
                .thenReturn(sampleTechnician());
 
        // Act & Assert
        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(validInput())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.technicianId").value(1))
                .andExpect(jsonPath("$.specialization").value("PLUMBING"));
 
        verify(technicianService, times(1)).createTechnician(any(TechnicianInputDTO.class));
    }
 
    @Test
    @DisplayName("POST technician with blank city -> 400 (validation) and service not invoked")
    void shouldReturn400WhenCityIsBlank() throws Exception {
        // Arrange
        TechnicianInputDTO invalid = validInput();
        invalid.setCity("");
 
        // Act & Assert
        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(invalid)))
                .andExpect(status().isBadRequest());
 
        verifyNoInteractions(technicianService);
    }
 
    @Test
    @DisplayName("POST technician for missing user -> 404 Not Found")
    void shouldReturn404WhenUserNotFound() throws Exception {
        // Arrange
        when(technicianService.createTechnician(any(TechnicianInputDTO.class)))
                .thenThrow(new UserIdNotFoundException("User not found"));
 
        // Act & Assert
        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(validInput())))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("User not found")));
 
        verify(technicianService, times(1)).createTechnician(any(TechnicianInputDTO.class));
    }
 
    @Test
    @DisplayName("GET technician by id -> 200 with technician")
    void shouldReturn200WhenTechnicianExists() throws Exception {
        // Arrange
        when(technicianService.getTechnicianById(1)).thenReturn(sampleTechnician());
 
        // Act & Assert
        mockMvc.perform(get(BASE_URL + "/{technicianid}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.technicianId").value(1))
                .andExpect(jsonPath("$.city").value("Chennai"));
 
        verify(technicianService, times(1)).getTechnicianById(1);
    }
 
    @Test
    @DisplayName("GET technician by id for missing technician -> 404 Not Found")
    void shouldReturn404WhenTechnicianNotFound() throws Exception {
        // Arrange
        when(technicianService.getTechnicianById(99))
                .thenThrow(new TechnicianNotFoundException("Technician not found"));
 
        // Act & Assert
        mockMvc.perform(get(BASE_URL + "/{technicianid}", 99))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Technician not found")));
 
        verify(technicianService, times(1)).getTechnicianById(99);
    }
 
    @Test
    @DisplayName("GET technician by id with non-numeric id -> 400 and service not invoked")
    void shouldReturn400WhenTechnicianIdNotNumeric() throws Exception {
        // Act & Assert
        mockMvc.perform(get(BASE_URL + "/{technicianid}", "abc"))
                .andExpect(status().isBadRequest());
 
        verifyNoInteractions(technicianService);
    }
 
    @Test
    @DisplayName("GET all technicians with default paging -> 200 with list")
    void shouldReturn200WhenTechniciansExist() throws Exception {
        // Arrange
        when(technicianService.getAllTechnicians(0, 5)).thenReturn(List.of(sampleTechnician()));
 
        // Act & Assert
        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].technicianId").value(1));
 
        verify(technicianService, times(1)).getAllTechnicians(0, 5);
    }
 
    @Test
    @DisplayName("GET all technicians with custom paging -> 200 with list")
    void shouldReturn200WhenTechniciansExistWithCustomPaging() throws Exception {
        // Arrange
        when(technicianService.getAllTechnicians(2, 20)).thenReturn(List.of(sampleTechnician()));
 
        // Act & Assert
        mockMvc.perform(get(BASE_URL)
                        .param("pageNo", "2")
                        .param("pageSize", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
 
        verify(technicianService, times(1)).getAllTechnicians(2, 20);
    }
 
    @Test
    @DisplayName("PUT technician -> 200 with updated technician")
    void shouldReturn200WhenTechnicianUpdated() throws Exception {
        // Arrange
        when(technicianService.updateTechnician(eq(1), any(TechnicianInputDTO.class)))
                .thenReturn(sampleTechnician());
 
        // Act & Assert
        mockMvc.perform(put(BASE_URL + "/{technicianId}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(validInput())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.technicianId").value(1));
 
        verify(technicianService, times(1)).updateTechnician(eq(1), any(TechnicianInputDTO.class));
    }
 
    @Test
    @DisplayName("PUT technician for missing technician -> 404 Not Found")
    void shouldReturn404WhenUpdatingMissingTechnician() throws Exception {
        // Arrange
        when(technicianService.updateTechnician(eq(99), any(TechnicianInputDTO.class)))
                .thenThrow(new TechnicianNotFoundException("Technician not found"));
 
        // Act & Assert
        mockMvc.perform(put(BASE_URL + "/{technicianId}", 99)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(validInput())))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Technician not found")));
 
        verify(technicianService, times(1)).updateTechnician(eq(99), any(TechnicianInputDTO.class));
    }
 
    @Test
    @DisplayName("PATCH deactivate technician -> 200 with confirmation message")
    void shouldReturn200WhenTechnicianDeactivated() throws Exception {
        // Arrange
        doNothing().when(technicianService).deactivateTechnician(1);
 
        // Act & Assert
        mockMvc.perform(patch(BASE_URL + "/{technicianId}/deactivate", 1))
                .andExpect(status().isOk())
                .andExpect(content().string("Technician deactivated successfully"));
 
        verify(technicianService, times(1)).deactivateTechnician(1);
    }
 
    @Test
    @DisplayName("PATCH deactivate missing technician -> 404 Not Found")
    void shouldReturn404WhenDeactivatingMissingTechnician() throws Exception {
        // Arrange
        doThrow(new TechnicianNotFoundException("Technician not found"))
                .when(technicianService).deactivateTechnician(99);
 
        // Act & Assert
        mockMvc.perform(patch(BASE_URL + "/{technicianId}/deactivate", 99))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Technician not found")));
 
        verify(technicianService, times(1)).deactivateTechnician(99);
    }
 
    @Test
    @DisplayName("GET available technicians -> 200 with list")
    void shouldReturn200WhenAvailableTechniciansExist() throws Exception {
        // Arrange
        when(technicianService.getAvailableTechnicians()).thenReturn(List.of(sampleTechnician()));
 
        // Act & Assert
        mockMvc.perform(get(BASE_URL + "/available"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].available").value(true));
 
        verify(technicianService, times(1)).getAvailableTechnicians();
    }
 
    @Test
    @DisplayName("GET available technicians when none -> 200 with empty list")
    void shouldReturn200WithEmptyListWhenNoAvailableTechnicians() throws Exception {
        // Arrange
        when(technicianService.getAvailableTechnicians()).thenReturn(Collections.emptyList());
 
        // Act & Assert
        mockMvc.perform(get(BASE_URL + "/available"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
 
        verify(technicianService, times(1)).getAvailableTechnicians();
    }
 
    @Test
    @DisplayName("GET technicians by city -> 200 with list")
    void shouldReturn200WhenTechniciansByCityExist() throws Exception {
        // Arrange
        when(technicianService.getTechnicianByCity("Chennai")).thenReturn(List.of(sampleTechnician()));
 
        // Act & Assert
        mockMvc.perform(get(BASE_URL + "/city/{city}", "Chennai"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].city").value("Chennai"));
 
        verify(technicianService, times(1)).getTechnicianByCity("Chennai");
    }
 
    @Test
    @DisplayName("GET technicians by specialization -> 200 with list")
    void shouldReturn200WhenTechniciansBySpecializationExist() throws Exception {
        // Arrange
        when(technicianService.getTechnicianBySpecialiaztion("PLUMBING"))
                .thenReturn(List.of(sampleTechnician()));
 
        // Act & Assert
        mockMvc.perform(get(BASE_URL + "/specialization/{specialization}", "PLUMBING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].specialization").value("PLUMBING"));
 
        verify(technicianService, times(1)).getTechnicianBySpecialiaztion("PLUMBING");
    }
}