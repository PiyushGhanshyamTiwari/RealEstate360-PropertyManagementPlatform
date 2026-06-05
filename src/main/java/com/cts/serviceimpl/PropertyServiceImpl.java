package com.cts.serviceimpl;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.cts.dto.PropertyInputDTO;
import com.cts.dto.PropertyOutputDTO;
import com.cts.entity.Property;
import com.cts.entity.User;
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
	public PropertyOutputDTO addProperty(PropertyInputDTO propertyInputDTO, int ownerId) {
		User user = userRepository.findById(ownerId).orElse(null);
		Property property = propertyMapper.convertToProperty(propertyInputDTO);
		property.setCreatedAt(LocalDate.now());
		property=propertyRepository.save(property);
		return propertyMapper.convertToOutputDTO(property);
	}

	@Override
	public List<PropertyOutputDTO> getAllProperties() {
		
		return propertyRepository.findAll()
				.stream()
				.map(property->propertyMapper.convertToOutputDTO(property))
				.collect(Collectors.toList());
	}

	@Override
	public List<PropertyOutputDTO> getPropertyByCity(String propertyCity) {
		
		return propertyRepository.findByPropertyCity(propertyCity)
				.stream()
				.map(property->propertyMapper.convertToOutputDTO(property))
				.collect(Collectors.toList());
	}

	@Override
	public List<PropertyOutputDTO> getPropertyByState(String propertyState) {
		
		return propertyRepository.findByPropertyState(propertyState)
				.stream()
				.map(property->propertyMapper.convertToOutputDTO(property))
				.collect(Collectors.toList());
	}

	@Override
	public List<PropertyOutputDTO> getPropertyByOwnerId(int ownerId) {
		
		return propertyRepository.findByOwnerId(ownerId)
				.stream()
				.map(property->propertyMapper.convertToOutputDTO(property))
				.collect(Collectors.toList());
	}

	
	
}
