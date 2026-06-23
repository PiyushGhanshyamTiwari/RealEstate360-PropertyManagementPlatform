package com.cts.serviceimpl;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.cts.annotation.Audit;

import com.cts.constants.AuditActions;
import com.cts.dto.PropertyInputDTO;
import com.cts.dto.PropertyOutputDTO;
import com.cts.entity.Property;
import com.cts.entity.User;
import com.cts.exception.OwnerIdNotFoundException;
import com.cts.exception.UserIdNotFoundException;
import com.cts.mapper.PropertyMapper;
import com.cts.repository.PropertyRepository;
import com.cts.repository.UserRepository;
import com.cts.service.PropertyService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class PropertyServiceImpl implements PropertyService {

    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;
    private final PropertyMapper propertyMapper;
   

    @Override
    @Audit(action = AuditActions.CREATE_PROPERTY, resourceType = "Property")
    public PropertyOutputDTO addProperty(PropertyInputDTO propertyInputDTO, int ownerId) {

        User user = userRepository.findById(ownerId).orElse(null);

        if (user == null) {

            throw new UserIdNotFoundException("Owner Id not found");

        }

        Property property = propertyMapper.convertToProperty(propertyInputDTO, user);

        property = propertyRepository.save(property);

        return propertyMapper.convertToPropertyOutputDTO(property);

    }

    @Override
    public List<PropertyOutputDTO> findPropertyByCity(String propertyCity) {
        return propertyRepository.findByPropertyCity(propertyCity)
                .stream()
                .map(property -> propertyMapper.convertToPropertyOutputDTO(property))
                .collect(Collectors.toList());
    }

    @Override
    public List<PropertyOutputDTO> findPropertyByState(String propertyState) {
        return propertyRepository.findByPropertyState(propertyState)
                .stream()
                .map(property -> propertyMapper.convertToPropertyOutputDTO(property))
                .collect(Collectors.toList());
    }

    @Override
    public List<PropertyOutputDTO> findPropertyByOwnerId(int ownerId) {
    	List<Property> properties = propertyRepository.findByOwnerId(ownerId); 
    	if(properties.isEmpty()) {

             throw new OwnerIdNotFoundException("Owner Id not found");

         }
        return propertyRepository.findByOwnerId(ownerId)
                .stream()
                .map(property -> propertyMapper.convertToPropertyOutputDTO(property))
                .collect(Collectors.toList());
    }
}
