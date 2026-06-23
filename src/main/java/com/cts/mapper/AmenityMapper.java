package com.cts.mapper;

import org.springframework.stereotype.Component;

import com.cts.dto.AmenityInputDTO;
import com.cts.dto.AmenityOutputDTO;
import com.cts.entity.Amenity;
import com.cts.entity.Unit;

import java.time.LocalDate;

@Component
public class AmenityMapper {

    // Convert Input DTO → Entity
    public Amenity convertToAmenity(AmenityInputDTO amenityInputDTO, Unit unit) {
        Amenity amenity = new Amenity();
        amenity.setUnit(unit);
        amenity.setName(amenityInputDTO.getName());
        amenity.setDescription(amenityInputDTO.getDescription());
        amenity.setCreatedAt(LocalDate.now());
        return amenity;
    }

    // Convert Entity → Output DTO
    public AmenityOutputDTO convertToAmenityOutputDTO(Amenity amenity) {
        AmenityOutputDTO response = new AmenityOutputDTO();
        response.setAmenityId(amenity.getAmenityId());
        response.setUnitId(amenity.getUnit() != null ? amenity.getUnit().getUnitId() : 0);
        response.setName(amenity.getName());
        response.setDescription(amenity.getDescription());
        response.setCreatedAt(amenity.getCreatedAt());
        return response;
    }
}
