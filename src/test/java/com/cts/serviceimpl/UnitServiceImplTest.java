package com.cts.serviceimpl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.cts.exception.UnitIdNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.cts.dto.UnitInputDTO;
import com.cts.dto.UnitOutputDTO;
import com.cts.entity.Property;
import com.cts.entity.Unit;
import com.cts.entity.User;
import com.cts.enums.UnitStatus;
import com.cts.exception.PropertyIdNotFoundException;
import com.cts.exception.UserIdNotFoundException;
import com.cts.mapper.UnitMapper;
import com.cts.repository.PropertyRepository;
import com.cts.repository.UnitRepository;
import com.cts.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UnitServiceImplTest {

    @Mock
    private UnitRepository unitRepository;

    @Mock
    private PropertyRepository propertyRepository;

    @Mock
    private UnitMapper unitMapper;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UnitServiceImpl unitService;

    @AfterEach
    void cleanup() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void testAddUnitSuccess() {

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "owner@test.com", null));

        UnitInputDTO dto = new UnitInputDTO();
        dto.setPropertyId(1);

        User owner = new User();
        owner.setUserId(10);

        Property property = new Property();
        property.setPropertyId(1);
        property.setUser(owner);

        Unit unit = new Unit();
        UnitOutputDTO output = new UnitOutputDTO();

        when(propertyRepository.findById(1))
                .thenReturn(Optional.of(property));

        when(userRepository.findUserByEmail("owner@test.com"))
                .thenReturn(owner);

        when(unitMapper.convertToUnit(dto, property))
                .thenReturn(unit);

        when(unitRepository.save(unit))
                .thenReturn(unit);

        when(unitMapper.convertToUnitOutputDTO(unit))
                .thenReturn(output);

        UnitOutputDTO result = unitService.addUnit(dto);

        assertEquals(output, result);
    }

    @Test
    void testAddUnitPropertyNotFound() {

        UnitInputDTO dto = new UnitInputDTO();
        dto.setPropertyId(1);

        when(propertyRepository.findById(1))
                .thenReturn(Optional.empty());

        assertThrows(
                PropertyIdNotFoundException.class,
                () -> unitService.addUnit(dto));
    }

    @Test
    void testAddUnitUserNotFound() {

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "owner@test.com", null));

        UnitInputDTO dto = new UnitInputDTO();
        dto.setPropertyId(1);

        Property property = new Property();
        property.setUser(new User());

        when(propertyRepository.findById(1))
                .thenReturn(Optional.of(property));

        when(userRepository.findUserByEmail("owner@test.com"))
                .thenReturn(null);

        assertThrows(
                UserIdNotFoundException.class,
                () -> unitService.addUnit(dto));
    }

    @Test
    void testAddUnitAccessDenied() {

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "owner@test.com", null));

        UnitInputDTO dto = new UnitInputDTO();
        dto.setPropertyId(1);

        User propertyOwner = new User();
        propertyOwner.setUserId(1);

        User loggedInUser = new User();
        loggedInUser.setUserId(2);

        Property property = new Property();
        property.setUser(propertyOwner);

        when(propertyRepository.findById(1))
                .thenReturn(Optional.of(property));

        when(userRepository.findUserByEmail("owner@test.com"))
                .thenReturn(loggedInUser);

        assertThrows(
                AccessDeniedException.class,
                () -> unitService.addUnit(dto));
    }

    @Test
    void testFindAllUnit() {

        Unit unit = new Unit();
        UnitOutputDTO dto = new UnitOutputDTO();

        when(unitRepository.findAll())
                .thenReturn(List.of(unit));

        when(unitMapper.convertToUnitOutputDTO(unit))
                .thenReturn(dto);

        List<UnitOutputDTO> result =
                unitService.findAllUnit();

        assertEquals(1, result.size());
    }

    @Test
    void testFilterUnits() {

        Property property = new Property();
        property.setPropertyId(1);
        property.setPropertyName("Green Villa");
        property.setPropertyCity("Chennai");

        Unit unit = new Unit();
        unit.setType("2BHK");
        unit.setProperty(property);
        unit.setStatus(UnitStatus.AVAILABLE);
        unit.setRentAmount(10000.0);

        UnitOutputDTO dto = new UnitOutputDTO();

        when(unitRepository.findAll())
                .thenReturn(List.of(unit));

        when(unitMapper.convertToUnitOutputDTO(unit))
                .thenReturn(dto);

        List<UnitOutputDTO> result =
                unitService.filterUnits(
                        "2BHK",
                        5000.0,
                        15000.0,
                        1,
                        "Green Villa",
                        "Chennai",
                        "AVAILABLE");

        assertEquals(1, result.size());
    }

    @Test
    void testFilterUnitsNoMatch() {

        when(unitRepository.findAll())
                .thenReturn(List.of());

        List<UnitOutputDTO> result =
                unitService.filterUnits(
                        "3BHK",
                        null,
                        null,
                        null,
                        null,
                        null,
                        null);

        assertTrue(result.isEmpty());
    }

    @Test
    void testUpdateUnitSuccessWithStatus() {

        UnitInputDTO dto = new UnitInputDTO();
        dto.setPropertyId(1);
        dto.setStatus(UnitStatus.AVAILABLE);

        Property property = new Property();

        Unit unit = new Unit();
        UnitOutputDTO output = new UnitOutputDTO();

        when(unitRepository.findById(1))
                .thenReturn(Optional.of(unit));

        when(propertyRepository.findById(1))
                .thenReturn(Optional.of(property));

        when(unitRepository.save(unit))
                .thenReturn(unit);

        when(unitMapper.convertToUnitOutputDTO(unit))
                .thenReturn(output);

        UnitOutputDTO result =
                unitService.updateUnit(1, dto);

        assertEquals(output, result);
    }

    @Test
    void testUpdateUnitSuccessWithoutStatus() {

        UnitInputDTO dto = new UnitInputDTO();
        dto.setPropertyId(1);
        dto.setStatus(null);

        Property property = new Property();
        Unit unit = new Unit();

        when(unitRepository.findById(1))
                .thenReturn(Optional.of(unit));

        when(propertyRepository.findById(1))
                .thenReturn(Optional.of(property));

        when(unitRepository.save(unit))
                .thenReturn(unit);

        when(unitMapper.convertToUnitOutputDTO(unit))
                .thenReturn(new UnitOutputDTO());

        assertNotNull(unitService.updateUnit(1, dto));
    }

    @Test
    void testUpdateUnitUnitNotFound() {

        UnitInputDTO dto = new UnitInputDTO();

        when(unitRepository.findById(1))
                .thenReturn(Optional.empty());

        assertThrows(
                UnitIdNotFoundException.class,
                () -> unitService.updateUnit(1, dto));
    }

    @Test
    void testUpdateUnitPropertyNotFound() {

        UnitInputDTO dto = new UnitInputDTO();
        dto.setPropertyId(1);

        when(unitRepository.findById(1))
                .thenReturn(Optional.of(new Unit()));

        when(propertyRepository.findById(1))
                .thenReturn(Optional.empty());

        assertThrows(
                PropertyIdNotFoundException.class,
                () -> unitService.updateUnit(1, dto));
    }
}