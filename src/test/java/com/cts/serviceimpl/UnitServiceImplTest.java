package com.cts.serviceimpl;

import com.cts.dto.UnitInputDTO;
import com.cts.dto.UnitOutputDTO;
import com.cts.entity.Property;
import com.cts.entity.Unit;
import com.cts.exception.PropertyIdNotFoundException;
import com.cts.mapper.UnitMapper;
import com.cts.repository.PropertyRepository;
import com.cts.repository.UnitRepository;
import com.cts.serviceimpl.UnitServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UnitServiceImplTest {

    @Mock
    private UnitRepository unitRepository;

    @Mock
    private PropertyRepository propertyRepository;

    @Mock
    private UnitMapper unitMapper;

    @InjectMocks
    private UnitServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Positive: addUnit succeeds
    @Test
    void testAddUnit_Success() {
        UnitInputDTO inputDto = new UnitInputDTO();
        inputDto.setPropertyId(1);

        Property property = new Property();
        property.setPropertyId(1);

        Unit unit = new Unit();
        unit.setUnitId(100);

        UnitOutputDTO outputDto = new UnitOutputDTO();
        outputDto.setUnitId(100);

        when(propertyRepository.findById(1)).thenReturn(Optional.of(property));
        when(unitMapper.convertToUnit(inputDto, property)).thenReturn(unit);
        when(unitRepository.save(unit)).thenReturn(unit);
        when(unitMapper.convertToUnitOutputDTO(unit)).thenReturn(outputDto);

        UnitOutputDTO result = service.addUnit(inputDto);

        assertNotNull(result);
        assertEquals(100, result.getUnitId());
        verify(unitRepository, times(1)).save(unit);
    }

    // Negative: property not found
    @Test
    void testAddUnit_PropertyNotFound() {
        UnitInputDTO inputDto = new UnitInputDTO();
        inputDto.setPropertyId(99);

        when(propertyRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(PropertyIdNotFoundException.class,
                () -> service.addUnit(inputDto));
    }

    // Positive: findAllUnit
    @Test
    void testFindAllUnit() {
        Unit unit = new Unit();
        unit.setUnitId(1);

        UnitOutputDTO dto = new UnitOutputDTO();
        dto.setUnitId(1);

        when(unitRepository.findAll()).thenReturn(Arrays.asList(unit));
        when(unitMapper.convertToUnitOutputDTO(unit)).thenReturn(dto);

        List<UnitOutputDTO> result = service.getAllUnit();

        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getUnitId());
    }

    // Positive: findUnitByType
    @Test
    void testFindUnitByType() {
        Unit unit = new Unit();
        unit.setUnitId(2);

        UnitOutputDTO dto = new UnitOutputDTO();
        dto.setUnitId(2);

        when(unitRepository.findUnitByType("2BHK")).thenReturn(Arrays.asList(unit));
        when(unitMapper.convertToUnitOutputDTO(unit)).thenReturn(dto);

        List<UnitOutputDTO> result = service.getUnitByType("2BHK");

        assertEquals(1, result.size());
        assertEquals(2, result.get(0).getUnitId());
    }

    // Positive: findUnitByAreaSqFt
    @Test
    void testFindUnitByAreaSqFt() {
        Unit unit = new Unit();
        unit.setUnitId(3);

        UnitOutputDTO dto = new UnitOutputDTO();
        dto.setUnitId(3);

        when(unitRepository.findUnitByAreaSqFt(1200)).thenReturn(Arrays.asList(unit));
        when(unitMapper.convertToUnitOutputDTO(unit)).thenReturn(dto);

        List<UnitOutputDTO> result = service.getUnitByAreaSqFt(1200);

        assertEquals(1, result.size());
        assertEquals(3, result.get(0).getUnitId());
    }

    // Positive: findUnitByFloor
    @Test
    void testFindUnitByFloor() {
        Unit unit = new Unit();
        unit.setUnitId(4);

        UnitOutputDTO dto = new UnitOutputDTO();
        dto.setUnitId(4);

        when(unitRepository.findUnitByFloor(2)).thenReturn(Arrays.asList(unit));
        when(unitMapper.convertToUnitOutputDTO(unit)).thenReturn(dto);

        List<UnitOutputDTO> result = service.getUnitByFloor(2);

        assertEquals(1, result.size());
        assertEquals(4, result.get(0).getUnitId());
    }

    // Positive: findUnitByPriceRange
    @Test
    void testFindUnitByPriceRange() {
        Unit unit = new Unit();
        unit.setUnitId(5);

        UnitOutputDTO dto = new UnitOutputDTO();
        dto.setUnitId(5);

        when(unitRepository.findUnitByRentAmountBetween(10000, 20000)).thenReturn(Arrays.asList(unit));
        when(unitMapper.convertToUnitOutputDTO(unit)).thenReturn(dto);

        List<UnitOutputDTO> result = service.findUnitByRentAmountBetween(10000, 20000);

        assertEquals(1, result.size());
        assertEquals(5, result.get(0).getUnitId());
    }

    // Positive: findUnitByPropertyId
    @Test
    void testFindUnitByPropertyId() {
        Unit unit = new Unit();
        unit.setUnitId(6);

        UnitOutputDTO dto = new UnitOutputDTO();
        dto.setUnitId(6);

        when(unitRepository.findUnitByPropertyId(1)).thenReturn(Arrays.asList(unit));
        when(unitMapper.convertToUnitOutputDTO(unit)).thenReturn(dto);

        List<UnitOutputDTO> result = service.findUnitByPropertyId(1);

        assertEquals(1, result.size());
        assertEquals(6, result.get(0).getUnitId());
    }
}
