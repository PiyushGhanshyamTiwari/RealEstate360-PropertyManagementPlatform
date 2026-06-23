package com.cts.serviceimpl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.cts.dto.*;
import com.cts.entity.*;
import com.cts.enums.TechnicianSpecialization;
import com.cts.exception.TechnicianNotFoundException;
import com.cts.exception.UserIdNotFoundException;
import com.cts.mapper.MaintenanceScheduleMapper;
import com.cts.mapper.TechnicianMapper;
import com.cts.repository.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TechnicianServiceImplTest {

    @Mock private TechnicianRepository technicianRepository;
    @Mock private UserRepository userRepository;
    @Mock private TechnicianMapper technicianMapper;
    @Mock private MaintenanceScheduleRepository scheduleRepository;
    @Mock private MaintenanceScheduleMapper scheduleMapper;

    @InjectMocks
    private TechnicianServiceImpl service;

    private User user;
    private Technician technician;
    private MaintenanceSchedule schedule;

    @BeforeEach
    void setup() {
        user = new User();
        user.setUserId(10);

        technician = new Technician();
        technician.setUser(user);
        technician.setSpecialization(TechnicianSpecialization.ELECTRICIAN);

        schedule = new MaintenanceSchedule();
        schedule.setTechnician(technician);
    }

   
    @Test
    void testCreateTechnicianSuccess() {
        TechnicianInputDTO input = new TechnicianInputDTO();
        input.setUserId(10);
        input.setSpecialization("ELECTRICIAN");
        input.setCity("Chennai");

        when(userRepository.findById(10)).thenReturn(Optional.of(user));
        when(technicianRepository.save(any())).thenReturn(technician);
        when(technicianMapper.convertToTechnicianOutputDTO(technician, user))
                .thenReturn(new TechnicianOutputDTO());

        TechnicianOutputDTO result = service.createTechnician(input);

        assertNotNull(result);
        verify(technicianRepository).save(any(Technician.class));
    }

   
    @Test
    void testCreateTechnicianUserNotFound() {
        TechnicianInputDTO input = new TechnicianInputDTO();
        input.setUserId(10);

        when(userRepository.findById(10)).thenReturn(Optional.empty());

        assertThrows(UserIdNotFoundException.class,
                () -> service.createTechnician(input));
    }

    
    @Test
    void testGetTechnicianByIdSuccess() {
        when(technicianRepository.findByUser_UserId(10))
                .thenReturn(Optional.of(technician));
        when(technicianMapper.convertToTechnicianOutputDTO(technician, user))
                .thenReturn(new TechnicianOutputDTO());

        TechnicianOutputDTO result = service.getTechnicianById(10);

        assertNotNull(result);
    }

     
    @Test
    void testGetTechnicianByIdNotFound() {
        when(technicianRepository.findByUser_UserId(10))
                .thenReturn(Optional.empty());

        assertThrows(TechnicianNotFoundException.class,
                () -> service.getTechnicianById(10));
    }

    
    @Test
    void testGetAllTechnicians() {
        when(technicianRepository.findAll()).thenReturn(List.of(technician));
        when(technicianMapper.convertToTechnicianOutputDTO(technician, user))
                .thenReturn(new TechnicianOutputDTO());

        List<TechnicianOutputDTO> result = service.getAllTechnicians();

        assertEquals(1, result.size());
    }

    
    @Test
    void testGetAllTechniciansEmpty() {
        when(technicianRepository.findAll()).thenReturn(Collections.emptyList());

        List<TechnicianOutputDTO> result = service.getAllTechnicians();

        assertTrue(result.isEmpty());
    }

    
    @Test
    void testGetTechnicianBySpecializationAndCity() {
        when(technicianRepository.findBySpecializationAndCityIgnoreCase(
                TechnicianSpecialization.ELECTRICIAN, "Chennai"))
                .thenReturn(List.of(technician));

        when(technicianMapper.convertToTechnicianOutputDTO(technician, user))
                .thenReturn(new TechnicianOutputDTO());

        List<TechnicianOutputDTO> result =
                service.getTechnicianBySpecializationAndCity("electrician", "Chennai");

        assertEquals(1, result.size());
    }

     
    @Test
    void testGetTechnicianBySpecializationAndCityEmpty() {
        when(technicianRepository.findBySpecializationAndCityIgnoreCase(
                TechnicianSpecialization.ELECTRICIAN, "Chennai"))
                .thenReturn(Collections.emptyList());

        List<TechnicianOutputDTO> result =
                service.getTechnicianBySpecializationAndCity("electrician", "Chennai");

        assertTrue(result.isEmpty());
    }

     
    @Test
    void testGetWorkHistorySuccess() {
        when(technicianRepository.findByUser_UserId(10))
                .thenReturn(Optional.of(technician));

        when(scheduleRepository.findByTechnicianUserUserId(10))
                .thenReturn(List.of(schedule));

        when(scheduleMapper.convertToResponseDTO(schedule))
                .thenReturn(new MaintenanceScheduleResponseDTO());

        List<MaintenanceScheduleResponseDTO> result =
                service.getWorkHistory(10);

        assertEquals(1, result.size());
    }

     
    @Test
    void testGetWorkHistoryTechnicianNotFound() {
        when(technicianRepository.findByUser_UserId(10))
                .thenReturn(Optional.empty());

        assertThrows(TechnicianNotFoundException.class,
                () -> service.getWorkHistory(10));
    }

     
    @Test
    void testGetWorkHistoryEmpty() {
        when(technicianRepository.findByUser_UserId(10))
                .thenReturn(Optional.of(technician));

        when(scheduleRepository.findByTechnicianUserUserId(10))
                .thenReturn(Collections.emptyList());

        List<MaintenanceScheduleResponseDTO> result =
                service.getWorkHistory(10);

        assertTrue(result.isEmpty());
    }
}
