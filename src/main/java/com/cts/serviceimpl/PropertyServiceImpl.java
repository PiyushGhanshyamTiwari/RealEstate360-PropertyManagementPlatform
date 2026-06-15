package com.cts.serviceimpl;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springdoc.core.converters.models.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.cts.dto.PropertyInputDTO;
import com.cts.dto.PropertyOutputDTO;
import com.cts.entity.Property;
import com.cts.entity.User;
import com.cts.exception.OwnerIdNotFoundException;
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
		Property property = propertyMapper.convertToProperty(propertyInputDTO,user);
		property.setCreatedAt(LocalDate.now());
		property=propertyRepository.save(property);
		return propertyMapper.convertToPropertyOutputDTO(property);
	}

	@Override
	public List<PropertyOutputDTO> getAllProperties() {
		
		return propertyRepository.findAll()
				.stream()
				.map(property->propertyMapper.convertToPropertyOutputDTO(property))
				.collect(Collectors.toList());
	}

	@Override
	public List<PropertyOutputDTO> getPropertyByCity(String propertyCity) {
		
		return propertyRepository.findByPropertyCity(propertyCity)
				.stream()
				.map(property->propertyMapper.convertToPropertyOutputDTO(property))
				.collect(Collectors.toList());
	}

	@Override
	public List<PropertyOutputDTO> getPropertyByState(String propertyState) {
		
		return propertyRepository.findByPropertyState(propertyState)
				.stream()
				.map(property->propertyMapper.convertToPropertyOutputDTO(property))
				.collect(Collectors.toList());
	}

	@Override
	public List<PropertyOutputDTO> getPropertyByOwnerId(int ownerId) {
		List<Property> list= propertyRepository.findByOwnerId(ownerId);
		if(list.isEmpty()) {
			throw new OwnerIdNotFoundException("Owner Not found");
		}
		return list
				.stream()
				.map(property->propertyMapper.convertToPropertyOutputDTO(property))
				.collect(Collectors.toList());
	
	}	
}
