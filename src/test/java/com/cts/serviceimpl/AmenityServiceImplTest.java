package com.cts.serviceimpl;

import com.cts.dto.AmenityInputDTO;
import com.cts.dto.AmenityOutputDTO;
import com.cts.entity.Amenity;
import com.cts.entity.Unit;
import com.cts.exception.UnitIdNotFoundException;
import com.cts.mapper.AmenityMapper;
import com.cts.repository.AmenityRepository;
import com.cts.repository.UnitRepository;
import com.cts.serviceimpl.AmenityServiceImpl;

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

public class AmenityServiceImplTest {

    @Mock
    private AmenityRepository amenityRepository;

    @Mock
    private UnitRepository unitRepository;

    @Mock
    private AmenityMapper amenityMapper;

    @InjectMocks
    private AmenityServiceImpl amenityService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddAmenity_Success() {
        AmenityInputDTO inputDto = new AmenityInputDTO();
        inputDto.setName("Pool");

        Unit unit = new Unit();
        unit.setUnitId(1);

        Amenity amenity = new Amenity();
        amenity.setName("Pool");

        AmenityOutputDTO outputDto = new AmenityOutputDTO();
        outputDto.setName("Pool");

        when(unitRepository.findById(1)).thenReturn(Optional.of(unit));
        when(amenityMapper.convertToAmenity(inputDto, unit)).thenReturn(amenity);
        when(amenityRepository.save(amenity)).thenReturn(amenity);
        when(amenityMapper.convertToAmenityOutputDTO(amenity)).thenReturn(outputDto);

        AmenityOutputDTO result = amenityService.addAmenity(inputDto, 1);

        assertNotNull(result);
        assertEquals("Pool", result.getName());
        verify(unitRepository, times(1)).findById(1);
        verify(amenityRepository, times(1)).save(amenity);
    }

    @Test
    void testAddAmenity_UnitNotFound() {
        AmenityInputDTO inputDto = new AmenityInputDTO();
        inputDto.setName("Gym");

        when(unitRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(UnitIdNotFoundException.class,
                () -> amenityService.addAmenity(inputDto, 99));
    }

    @Test
    void testGetAllAmenities() {
        Amenity amenity1 = new Amenity();
        amenity1.setName("Pool");
        Amenity amenity2 = new Amenity();
        amenity2.setName("Gym");

        AmenityOutputDTO dto1 = new AmenityOutputDTO();
        dto1.setName("Pool");
        AmenityOutputDTO dto2 = new AmenityOutputDTO();
        dto2.setName("Gym");

        when(amenityRepository.findAll()).thenReturn(Arrays.asList(amenity1, amenity2));
        when(amenityMapper.convertToAmenityOutputDTO(amenity1)).thenReturn(dto1);
        when(amenityMapper.convertToAmenityOutputDTO(amenity2)).thenReturn(dto2);

        List<AmenityOutputDTO> result = amenityService.getAllAmenities();

        assertEquals(2, result.size());
        verify(amenityRepository, times(1)).findAll();
    }

    @Test
    void testGetAmenitiesByUnit() {
        Amenity amenity = new Amenity();
        amenity.setName("Pool");

        AmenityOutputDTO dto = new AmenityOutputDTO();
        dto.setName("Pool");

        when(amenityRepository.findByUnitId(1)).thenReturn(Arrays.asList(amenity));
        when(amenityMapper.convertToAmenityOutputDTO(amenity)).thenReturn(dto);

        List<AmenityOutputDTO> result = amenityService.getAmenitiesByUnit(1);

        assertEquals(1, result.size());
        assertEquals("Pool", result.get(0).getName());
        verify(amenityRepository, times(1)).findByUnitId(1);
    }

    @Test
    void testGetAmenitiesByName() {
        Amenity amenity = new Amenity();
        amenity.setName("Gym");

        AmenityOutputDTO dto = new AmenityOutputDTO();
        dto.setName("Gym");

        when(amenityRepository.findByName("Gym")).thenReturn(Arrays.asList(amenity));
        when(amenityMapper.convertToAmenityOutputDTO(amenity)).thenReturn(dto);

        List<AmenityOutputDTO> result = amenityService.getAmenitiesByName("Gym");

        assertEquals(1, result.size());
        assertEquals("Gym", result.get(0).getName());
        verify(amenityRepository, times(1)).findByName("Gym");
    }
}
