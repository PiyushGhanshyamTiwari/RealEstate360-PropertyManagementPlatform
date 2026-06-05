package com.cts.service;

import java.util.List;

import com.cts.dto.AmenityInputDTO;
import com.cts.dto.AmenityOutputDTO;
import com.cts.entity.Amenity;

public interface AmenityService {

	public AmenityOutputDTO addAmenity(AmenityInputDTO amenity, int unitId);

	public List<AmenityOutputDTO> getAllAmenities();

	public List<AmenityOutputDTO> getAmenitiesByUnit(int unitId);

	public List<AmenityOutputDTO> getAmenitiesByName(String name);

}
