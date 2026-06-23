package com.cts.serviceimpl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.cts.dto.PropertyInputDTO;
import com.cts.dto.PropertyOutputDTO;
import com.cts.entity.Property;
import com.cts.entity.User;
import com.cts.exception.OwnerIdNotFoundException;
import com.cts.exception.UserIdNotFoundException;
import com.cts.mapper.PropertyMapper;
import com.cts.repository.PropertyRepository;
import com.cts.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class PropertyServiceImplTest {

    @Mock
    private PropertyRepository propertyRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PropertyMapper propertyMapper;

    @InjectMocks
    private PropertyServiceImpl propertyService;

    @Test
    void addProperty_Success() {

        int ownerId = 1;

        User user = new User();
        user.setUserId(ownerId);

        PropertyInputDTO input = new PropertyInputDTO();
        Property property = new Property();
        PropertyOutputDTO output = new PropertyOutputDTO();

        when(userRepository.findById(ownerId))
                .thenReturn(Optional.of(user));

        when(propertyMapper.convertToProperty(input, user))
                .thenReturn(property);

        when(propertyRepository.save(property))
                .thenReturn(property);

        when(propertyMapper.convertToPropertyOutputDTO(property))
                .thenReturn(output);

        PropertyOutputDTO result =
                propertyService.addProperty(input, ownerId);

        assertNotNull(result);

        verify(propertyRepository).save(property);
    }

    @Test
    void addProperty_UserNotFound() {

        when(userRepository.findById(1))
                .thenReturn(Optional.empty());

        assertThrows(UserIdNotFoundException.class,
                () -> propertyService.addProperty(
                        new PropertyInputDTO(),
                        1));
    }

    @Test
    void findPropertyByCity_Success() {

        Property property = new Property();
        PropertyOutputDTO dto = new PropertyOutputDTO();

        when(propertyRepository.findByPropertyCity("Chennai"))
                .thenReturn(List.of(property));

        when(propertyMapper.convertToPropertyOutputDTO(property))
                .thenReturn(dto);

        List<PropertyOutputDTO> result =
                propertyService.findPropertyByCity("Chennai");

        assertEquals(1, result.size());
    }

    @Test
    void findPropertyByCity_EmptyList() {

        when(propertyRepository.findByPropertyCity("Chennai"))
                .thenReturn(Collections.emptyList());

        List<PropertyOutputDTO> result =
                propertyService.findPropertyByCity("Chennai");

        assertTrue(result.isEmpty());
    }

    @Test
    void findPropertyByState_Success() {

        Property property = new Property();
        PropertyOutputDTO dto = new PropertyOutputDTO();

        when(propertyRepository.findByPropertyState("Tamil Nadu"))
                .thenReturn(List.of(property));

        when(propertyMapper.convertToPropertyOutputDTO(property))
                .thenReturn(dto);

        List<PropertyOutputDTO> result =
                propertyService.findPropertyByState("Tamil Nadu");

        assertEquals(1, result.size());
    }

    @Test
    void findPropertyByState_EmptyList() {

        when(propertyRepository.findByPropertyState("Tamil Nadu"))
                .thenReturn(Collections.emptyList());

        List<PropertyOutputDTO> result =
                propertyService.findPropertyByState("Tamil Nadu");

        assertTrue(result.isEmpty());
    }

    @Test
    void findPropertyByOwnerId_Success() {

        Property property = new Property();
        PropertyOutputDTO dto = new PropertyOutputDTO();

        when(propertyRepository.findByOwnerId(1))
                .thenReturn(List.of(property));

        when(propertyMapper.convertToPropertyOutputDTO(property))
                .thenReturn(dto);

        List<PropertyOutputDTO> result =
                propertyService.findPropertyByOwnerId(1);

        assertEquals(1, result.size());

        verify(propertyRepository, times(2))
                .findByOwnerId(1);
    }

    @Test
    void findPropertyByOwnerId_NotFound() {

        when(propertyRepository.findByOwnerId(1))
                .thenReturn(Collections.emptyList());

        assertThrows(OwnerIdNotFoundException.class,
                () -> propertyService.findPropertyByOwnerId(1));
    }
}