package com.cts.mapper;

import org.springframework.stereotype.Component;

import com.cts.dto.PropertyInputDTO;
import com.cts.dto.PropertyOutputDTO;
import com.cts.entity.Property;

@Component
public class PropertyMapper {
	
	public Property convertToProperty(PropertyInputDTO propertyInputDTO) {
		Property property = new Property();
		property.setPropertyId(propertyInputDTO.getPropertyId());
		property.setPropertyName(propertyInputDTO.getPropertyName());
		property.setPropertyAddress(propertyInputDTO.getPropertyAddress());
		property.setPropertyCity(propertyInputDTO.getPropertyCity());
		property.setPropertyState(propertyInputDTO.getPropertyState());
		property.setPropertyPostalCode(propertyInputDTO.getPropertyPostalCode());
		property.setPropertyCountry(propertyInputDTO.getPropertyCountry());
		return property;
		
	}
	
	public PropertyOutputDTO convertToOutputDTO(Property property) {
		PropertyOutputDTO response = new PropertyOutputDTO();
		response.setPropertyId(property.getPropertyId());
		response.setPropertyName(property.getPropertyName());
		response.setPropertyAddress(property.getPropertyAddress());
		response.setPropertyCity(property.getPropertyCity());
		response.setPropertyState(property.getPropertyState());
		response.setPropertyPostalCode(property.getPropertyPostalCode());
		response.setPropertyCountry(property.getPropertyCountry());
		response.setCreatedAt(property.getCreatedAt());
		response.setUpdatedAt(property.getUpdatedAt());
		
		return response;
	}

}
