package com.cts.service;

import java.util.List;

import com.cts.dto.PropertyInputDTO;
import com.cts.dto.PropertyOutputDTO;
import com.cts.entity.Property;

public interface PropertyService {

	PropertyOutputDTO addProperty(PropertyInputDTO propertyInputDTO, int ownerId);

	List<PropertyOutputDTO> getAllProperties();

	List<PropertyOutputDTO> getPropertyByState(String state);

	List<PropertyOutputDTO> getPropertyByOwnerId(int ownerId);

	List<PropertyOutputDTO> getPropertyByCity(String city);

	

}
