package com.cts.serviceimpl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

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

    @Test
    void addUnit_Success() {

        UnitInputDTO input = new UnitInputDTO();
        input.setPropertyId(1);

        User owner = new User();
        owner.setUserId(10);

        Property property = new Property();
        property.setUser(owner);

        Unit unit = new Unit();

        UnitOutputDTO outputDTO = new UnitOutputDTO();

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "owner@test.com",
                        null));

        when(propertyRepository.findById(1))
                .thenReturn(Optional.of(property));

        when(userRepository.findUserByEmail("owner@test.com"))
                .thenReturn(owner);

        when(unitMapper.convertToUnit(input, property))
                .thenReturn(unit);

        when(unitRepository.save(unit))
                .thenReturn(unit);

        when(unitMapper.convertToUnitOutputDTO(unit))
                .thenReturn(outputDTO);

        UnitOutputDTO result = unitService.addUnit(input);

        assertNotNull(result);

        verify(unitRepository).save(unit);
    }

    @Test
    void addUnit_PropertyNotFound() {

        UnitInputDTO input = new UnitInputDTO();
        input.setPropertyId(1);

        when(propertyRepository.findById(1))
                .thenReturn(Optional.empty());

        assertThrows(PropertyIdNotFoundException.class,
                () -> unitService.addUnit(input));
    }

    @Test
    void addUnit_UserNotFound() {

        UnitInputDTO input = new UnitInputDTO();
        input.setPropertyId(1);

        User owner = new User();
        owner.setUserId(10);

        Property property = new Property();
        property.setUser(owner);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "owner@test.com",
                        null));

        when(propertyRepository.findById(1))
                .thenReturn(Optional.of(property));

        when(userRepository.findUserByEmail("owner@test.com"))
                .thenReturn(null);

        assertThrows(UserIdNotFoundException.class,
                () -> unitService.addUnit(input));
    }

    @Test
    void addUnit_AccessDenied() {

        UnitInputDTO input = new UnitInputDTO();
        input.setPropertyId(1);

        User owner = new User();
        owner.setUserId(10);

        User loggedInUser = new User();
        loggedInUser.setUserId(20);

        Property property = new Property();
        property.setUser(owner);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "owner@test.com",
                        null));

        when(propertyRepository.findById(1))
                .thenReturn(Optional.of(property));

        when(userRepository.findUserByEmail("owner@test.com"))
                .thenReturn(loggedInUser);

        assertThrows(AccessDeniedException.class,
                () -> unitService.addUnit(input));
    }

    @Test
    void findAllUnit() {

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
    void findUnitByType() {

        Unit unit = new Unit();
        UnitOutputDTO dto = new UnitOutputDTO();

        when(unitRepository.findUnitByType("2BHK"))
                .thenReturn(List.of(unit));

        when(unitMapper.convertToUnitOutputDTO(unit))
                .thenReturn(dto);

        List<UnitOutputDTO> result =
                unitService.findUnitByType("2BHK");

        assertEquals(1, result.size());
    }

    @Test
    void findUnitByAreaSqFt() {

        Unit unit = new Unit();
        UnitOutputDTO dto = new UnitOutputDTO();

        when(unitRepository.findUnitByAreaSqFt(1200))
                .thenReturn(List.of(unit));

        when(unitMapper.convertToUnitOutputDTO(unit))
                .thenReturn(dto);

        List<UnitOutputDTO> result =
                unitService.findUnitByAreaSqFt(1200);

        assertEquals(1, result.size());
    }

    @Test
    void findUnitByFloor() {

        Unit unit = new Unit();
        UnitOutputDTO dto = new UnitOutputDTO();

        when(unitRepository.findUnitByFloor(2))
                .thenReturn(List.of(unit));

        when(unitMapper.convertToUnitOutputDTO(unit))
                .thenReturn(dto);

        List<UnitOutputDTO> result =
                unitService.findUnitByFloor(2);

        assertEquals(1, result.size());
    }

    @Test
    void findUnitByPriceRange() {

        Unit unit = new Unit();
        UnitOutputDTO dto = new UnitOutputDTO();

        when(unitRepository.findUnitByPriceRange(1000, 5000))
                .thenReturn(List.of(unit));

        when(unitMapper.convertToUnitOutputDTO(unit))
                .thenReturn(dto);

        List<UnitOutputDTO> result =
                unitService.findUnitByPriceRange(1000, 5000);

        assertEquals(1, result.size());
    }

    @Test
    void findUnitByPropertyId() {

        Unit unit = new Unit();
        UnitOutputDTO dto = new UnitOutputDTO();

        when(unitRepository.findUnitByPropertyId(1))
                .thenReturn(List.of(unit));

        when(unitMapper.convertToUnitOutputDTO(unit))
                .thenReturn(dto);

        List<UnitOutputDTO> result =
                unitService.findUnitByPropertyId(1);

        assertEquals(1, result.size());
    }

    @Test
    void findUnitByStatus() {

        Unit unit = new Unit();
        UnitOutputDTO dto = new UnitOutputDTO();

        when(unitRepository.findByStatus(UnitStatus.AVAILABLE))
                .thenReturn(List.of(unit));

        when(unitMapper.convertToUnitOutputDTO(unit))
                .thenReturn(dto);

        List<UnitOutputDTO> result =
                unitService.findUnitByStatus("AVAILABLE");

        assertEquals(1, result.size());
    }

    @Test
    void findUnitByStatus_LowerCaseInput() {

        Unit unit = new Unit();
        UnitOutputDTO dto = new UnitOutputDTO();

        when(unitRepository.findByStatus(UnitStatus.AVAILABLE))
                .thenReturn(List.of(unit));

        when(unitMapper.convertToUnitOutputDTO(unit))
                .thenReturn(dto);

        List<UnitOutputDTO> result =
                unitService.findUnitByStatus("available");

        assertEquals(1, result.size());
    }
}