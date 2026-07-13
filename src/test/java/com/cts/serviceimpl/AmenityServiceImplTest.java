package com.cts.serviceimpl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.cts.dto.AmenityInputDTO;
import com.cts.dto.AmenityOutputDTO;
import com.cts.entity.Amenity;
import com.cts.entity.Unit;
import com.cts.repository.AmenityRepository;
import com.cts.repository.UnitRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AmenityServiceImplTest {

    @Mock
    private AmenityRepository amenityRepository;

    @Mock
    private UnitRepository unitRepository;

    @InjectMocks
    private AmenityServiceImpl service;

    private Unit unit;
    private Amenity amenity;

    @BeforeEach
    void setup() {
        unit = new Unit();
        unit.setUnitId(1);
        unit.setType("Apartment");
        unit.setAreaSqFt(1200);
        unit.setFloor(2);
        unit.setRentAmount(15000);
        unit.setDepositAmount(50000);

        amenity = new Amenity();
        amenity.setAmenityId(10);
        amenity.setUnit(unit);
        amenity.setName("Gym");
        amenity.setDescription("Fully equipped gym");
        amenity.setCreatedAt(LocalDate.now());
    }

    @Test
    void testAddAmenitySuccess() {
        AmenityInputDTO input = new AmenityInputDTO();
        input.setName("Gym");
        input.setDescription("Fully equipped gym");

        when(unitRepository.findById(1)).thenReturn(Optional.of(unit));
        when(amenityRepository.save(any(Amenity.class))).thenReturn(amenity);

        AmenityOutputDTO result = service.addAmenity(input, 1);

        assertNotNull(result);
        assertEquals("Gym", result.getName());
        assertEquals(1, result.getUnitId());

        verify(unitRepository).findById(1);
        verify(amenityRepository).save(any(Amenity.class));
    }

    @Test
    void testAddAmenityWhenUnitNotFound() {
        AmenityInputDTO input = new AmenityInputDTO();
        input.setName("Pool");
        input.setDescription("Swimming pool");

        when(unitRepository.findById(1)).thenReturn(Optional.empty());

        // this will still save with null unit (based on your logic)
        when(amenityRepository.save(any(Amenity.class))).thenReturn(amenity);

        AmenityOutputDTO result = service.addAmenity(input, 1);

        assertNotNull(result);

        verify(unitRepository).findById(1);
        verify(amenityRepository).save(any(Amenity.class));
    }

    @Test
    void testGetAllAmenities() {
        when(amenityRepository.findAll()).thenReturn(List.of(amenity));

        List<AmenityOutputDTO> result = service.getAllAmenities();

        assertEquals(1, result.size());
        assertEquals("Gym", result.get(0).getName());

        verify(amenityRepository).findAll();
    }

    @Test
    void testGetAllAmenitiesEmpty() {
        when(amenityRepository.findAll()).thenReturn(Collections.emptyList());

        List<AmenityOutputDTO> result = service.getAllAmenities();

        assertTrue(result.isEmpty());

        verify(amenityRepository).findAll();
    }

    @Test
    void testGetAmenitiesByUnit() {
        when(amenityRepository.findByUnitId(1)).thenReturn(List.of(amenity));

        List<AmenityOutputDTO> result = service.getAmenitiesByUnit(1);

        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getUnitId());

        verify(amenityRepository).findByUnitId(1);
    }

    @Test
    void testGetAmenitiesByUnitEmpty() {
        when(amenityRepository.findByUnitId(1)).thenReturn(Collections.emptyList());

        List<AmenityOutputDTO> result = service.getAmenitiesByUnit(1);

        assertTrue(result.isEmpty());

        verify(amenityRepository).findByUnitId(1);
    }

    @Test
    void testGetAmenitiesByName() {
        when(amenityRepository.findByName("Gym")).thenReturn(List.of(amenity));

        List<AmenityOutputDTO> result = service.getAmenitiesByName("Gym");

        assertEquals(1, result.size());
        assertEquals("Gym", result.get(0).getName());

        verify(amenityRepository).findByName("Gym");
    }

    @Test
    void testGetAmenitiesByNameEmpty() {
        when(amenityRepository.findByName("Unknown")).thenReturn(Collections.emptyList());

        List<AmenityOutputDTO> result = service.getAmenitiesByName("Unknown");

        assertTrue(result.isEmpty());

        verify(amenityRepository).findByName("Unknown");
    }
}
