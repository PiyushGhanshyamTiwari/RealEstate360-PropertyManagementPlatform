package com.cts.serviceimpl;

import com.cts.dto.TechnicianInputDTO;
import com.cts.dto.TechnicianOutputDTO;
import com.cts.entity.Technician;
import com.cts.entity.User;
import com.cts.enums.TechnicianSpecialization;
import com.cts.enums.TechnicianStatus;
import com.cts.exception.TechnicianNotFoundException;
import com.cts.exception.UserIdNotFoundException;
import com.cts.mapper.TechnicianMapper;
import com.cts.repository.TechnicianRepository;
import com.cts.repository.UserRepository;
import com.cts.serviceimpl.TechnicianServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TechnicianServiceImplTest {

    @Mock
    private TechnicianRepository technicianRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TechnicianMapper technicianMapper;

    @InjectMocks
    private TechnicianServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Positive: createTechnician
    @Test
    void testCreateTechnician_Success() {
        TechnicianInputDTO input = new TechnicianInputDTO();
        input.setUserId(1);
        input.setSpecialization("PLUMBER");
        input.setCity("Chennai");

        User user = new User();
        user.setUserId(1);

        Technician technician = new Technician();
        technician.setTechnicianId(100);
        technician.setUser(user);

        TechnicianOutputDTO output = new TechnicianOutputDTO();
        output.setTechnicianId(100);

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(technicianRepository.save(any(Technician.class))).thenReturn(technician);
        when(technicianMapper.convertToTechnicianOutputDTO(technician, user)).thenReturn(output);

        TechnicianOutputDTO result = service.createTechnician(input);

        assertNotNull(result);
        assertEquals(100, result.getTechnicianId());
    }

    // Negative: user not found
    @Test
    void testCreateTechnician_UserNotFound() {
        TechnicianInputDTO input = new TechnicianInputDTO();
        input.setUserId(99);
        input.setSpecialization("ELECTRICIAN");

        when(userRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(UserIdNotFoundException.class,
                () -> service.createTechnician(input));
    }

    // Positive: getTechnicianById
    @Test
    void testGetTechnicianById_Success() {
        User user = new User();
        Technician technician = new Technician();
        technician.setTechnicianId(100);
        technician.setUser(user);

        TechnicianOutputDTO dto = new TechnicianOutputDTO();
        dto.setTechnicianId(100);

        when(technicianRepository.findById(100)).thenReturn(Optional.of(technician));
        when(technicianMapper.convertToTechnicianOutputDTO(technician, user)).thenReturn(dto);

        TechnicianOutputDTO result = service.getTechnicianById(100);

        assertEquals(100, result.getTechnicianId());
    }

    // Negative: technician not found
    @Test
    void testGetTechnicianById_NotFound() {
        when(technicianRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(TechnicianNotFoundException.class,
                () -> service.getTechnicianById(999));
    }

    // Positive: getAllTechnicians
    @Test
    void testGetAllTechnicians() {
        User user = new User();
        Technician technician = new Technician();
        technician.setTechnicianId(1);
        technician.setUser(user);

        TechnicianOutputDTO dto = new TechnicianOutputDTO();
        dto.setTechnicianId(1);

        Page<Technician> page = new PageImpl<>(List.of(technician));

        when(technicianRepository.findAll(any(Pageable.class))).thenReturn(page);
        when(technicianMapper.convertToTechnicianOutputDTO(technician, user)).thenReturn(dto);

        List<TechnicianOutputDTO> result = service.getAllTechnicians(0, 10);

        assertEquals(1, result.size());
    }

    // Positive: deactivateTechnician
    @Test
    void testDeactivateTechnician_Success() {
        Technician technician = new Technician();
        technician.setTechnicianId(1);
        technician.setStatus(TechnicianStatus.ACTIVE);
        technician.setAvailable(true);

        when(technicianRepository.findById(1)).thenReturn(Optional.of(technician));
        when(technicianRepository.save(technician)).thenReturn(technician);

        service.deactivateTechnician(1);

        assertEquals(TechnicianStatus.INACTIVE, technician.getStatus());
        assertFalse(technician.getAvailable());
    }

    // Negative: deactivateTechnician not found
    @Test
    void testDeactivateTechnician_NotFound() {
        when(technicianRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(TechnicianNotFoundException.class,
                () -> service.deactivateTechnician(99));
    }

    // Positive: getAvailableTechnicians
    @Test
    void testGetAvailableTechnicians() {
        User user = new User();
        Technician technician = new Technician();
        technician.setTechnicianId(1);
        technician.setUser(user);

        TechnicianOutputDTO dto = new TechnicianOutputDTO();
        dto.setTechnicianId(1);

        when(technicianRepository.findByAvailableTrue()).thenReturn(Arrays.asList(technician));
        when(technicianMapper.convertToTechnicianOutputDTO(technician, user)).thenReturn(dto);

        List<TechnicianOutputDTO> result = service.getAvailableTechnicians();

        assertEquals(1, result.size());
    }

    // Positive: getTechnicianByCity
    @Test
    void testGetTechnicianByCity() {
        User user = new User();
        Technician technician = new Technician();
        technician.setTechnicianId(1);
        technician.setUser(user);

        TechnicianOutputDTO dto = new TechnicianOutputDTO();
        dto.setTechnicianId(1);

        when(technicianRepository.findByCityIgnoreCase("Chennai")).thenReturn(Arrays.asList(technician));
        when(technicianMapper.convertToTechnicianOutputDTO(technician, user)).thenReturn(dto);

        List<TechnicianOutputDTO> result = service.getTechnicianByCity("Chennai");

        assertEquals(1, result.size());
    }

    // Positive: getTechnicianBySpecialization
    @Test
    void testGetTechnicianBySpecialization() {
        User user = new User();
        Technician technician = new Technician();
        technician.setTechnicianId(1);
        technician.setUser(user);
        technician.setSpecialization(TechnicianSpecialization.PLUMBER);

        TechnicianOutputDTO dto = new TechnicianOutputDTO();
        dto.setTechnicianId(1);

        when(technicianRepository.findBySpecialization(TechnicianSpecialization.PLUMBER))
                .thenReturn(Arrays.asList(technician));
        when(technicianMapper.convertToTechnicianOutputDTO(technician, user)).thenReturn(dto);

        List<TechnicianOutputDTO> result = service.getTechnicianBySpecialiaztion("PLUMBER");

        assertEquals(1, result.size());
    }

    // Positive: updateTechnician
    @Test
    void testUpdateTechnician_Success() {
        TechnicianInputDTO input = new TechnicianInputDTO();
        input.setUserId(1);
        input.setSpecialization("ELECTRICIAN");
        input.setCity("Chennai");

        User user = new User();
        user.setUserId(1);

        Technician technician = new Technician();
        technician.setTechnicianId(1);

        TechnicianOutputDTO dto = new TechnicianOutputDTO();
        dto.setTechnicianId(1);

        when(technicianRepository.findById(1)).thenReturn(Optional.of(technician));
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(technicianRepository.save(technician)).thenReturn(technician);
        when(technicianMapper.convertToTechnicianOutputDTO(technician, user)).thenReturn(dto);

        TechnicianOutputDTO result = service.updateTechnician(1, input);

        assertEquals(1, result.getTechnicianId());
    }

    // Negative: updateTechnician technician not found
    @Test
    void testUpdateTechnician_TechnicianNotFound() {
        TechnicianInputDTO input = new TechnicianInputDTO();
        input.setUserId(1);

        when(technicianRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(TechnicianNotFoundException.class,
                () -> service.updateTechnician(99, input));
    }

    // Negative: updateTechnician user not found
    @Test
    void testUpdateTechnician_UserNotFound() {
        TechnicianInputDTO input = new TechnicianInputDTO();
        input.setUserId(99);
        input.setSpecialization("PLUMBER");

        Technician technician = new Technician();
        technician.setTechnicianId(1);

        // Technician exists, but user does not
        when(technicianRepository.findById(1)).thenReturn(Optional.of(technician));
        when(userRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(UserIdNotFoundException.class,
                () -> service.updateTechnician(1, input));
    }
}
