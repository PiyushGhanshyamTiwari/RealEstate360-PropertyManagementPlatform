package com.cts.serviceimpl;

import com.cts.dto.PropertyInputDTO;
import com.cts.dto.PropertyOutputDTO;
import com.cts.entity.Property;
import com.cts.entity.User;
import com.cts.exception.UserIdNotFoundException;
import com.cts.mapper.PropertyMapper;
import com.cts.repository.PropertyRepository;
import com.cts.repository.UserRepository;
import com.cts.serviceimpl.PropertyServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PropertyServiceImplTest {

    @Mock
    private PropertyRepository propertyRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PropertyMapper propertyMapper;

    @InjectMocks
    private PropertyServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Positive: addProperty succeeds
    @Test
    void testAddProperty_Success() {
        PropertyInputDTO inputDto = new PropertyInputDTO();
        User owner = new User();
        owner.setUserId(1);

        Property property = new Property();
        property.setPropertyId(100);

        PropertyOutputDTO outputDto = new PropertyOutputDTO();
        outputDto.setPropertyId(100);

        when(userRepository.findById(1)).thenReturn(Optional.of(owner));
        when(propertyMapper.convertToProperty(inputDto,owner)).thenReturn(property);
        when(propertyRepository.save(property)).thenReturn(property);
        when(propertyMapper.convertToPropertyOutputDTO(property)).thenReturn(outputDto);

        PropertyOutputDTO result = service.addProperty(inputDto, 1);

        assertNotNull(result);
        assertEquals(100, result.getPropertyId());
        verify(propertyRepository, times(1)).save(property);
    }

    // Negative: owner not found
    @Test
    void testAddProperty_OwnerNotFound() {
        PropertyInputDTO inputDto = new PropertyInputDTO();

        when(userRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(UserIdNotFoundException.class,
                () -> service.addProperty(inputDto, 99));
    }

    // Positive: findPropertyByCity
    @Test
    void testFindPropertyByCity() {
        Property property = new Property();
        property.setPropertyId(100);

        PropertyOutputDTO dto = new PropertyOutputDTO();
        dto.setPropertyId(100);

        when(propertyRepository.findByPropertyCity(eq("Chennai")))
                .thenReturn(Arrays.asList(property));
        when(propertyMapper.convertToPropertyOutputDTO(property)).thenReturn(dto);

        List<PropertyOutputDTO> result = service.getPropertyByCity("Chennai");

        assertEquals(1, result.size());
        assertEquals(100, result.get(0).getPropertyId());
    }

    // Positive: findPropertyByState
    @Test
    void testFindPropertyByState() {
        Property property = new Property();
        property.setPropertyId(200);

        PropertyOutputDTO dto = new PropertyOutputDTO();
        dto.setPropertyId(200);

        when(propertyRepository.findByPropertyState(eq("Tamil Nadu")))
                .thenReturn(Arrays.asList(property));
        when(propertyMapper.convertToPropertyOutputDTO(property)).thenReturn(dto);

        List<PropertyOutputDTO> result = service.getPropertyByState("Tamil Nadu");

        assertEquals(1, result.size());
        assertEquals(200, result.get(0).getPropertyId());
    }

    // Positive: findPropertyByOwnerId
    @Test
    void testFindPropertyByOwnerId() {
        Property property = new Property();
        property.setPropertyId(300);

        PropertyOutputDTO dto = new PropertyOutputDTO();
        dto.setPropertyId(300);

        when(propertyRepository.findByOwnerId(eq(1)))
                .thenReturn(Arrays.asList(property));
        when(propertyMapper.convertToPropertyOutputDTO(property)).thenReturn(dto);

        List<PropertyOutputDTO> result = service.getPropertyByOwnerId(1);

        assertEquals(1, result.size());
        assertEquals(300, result.get(0).getPropertyId());
    }
}
