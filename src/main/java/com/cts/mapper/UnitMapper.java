package com.cts.mapper;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

import com.cts.dto.AmenityOutputDTO;
import com.cts.enums.UnitStatus;
import com.cts.service.AmenityService;
import com.cts.service.PropertyPhotoService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import com.cts.dto.UnitInputDTO;
import com.cts.dto.UnitOutputDTO;
import com.cts.entity.Property;
import com.cts.entity.Unit;

@Component
@AllArgsConstructor
public class UnitMapper {
    private final AmenityService amenityService;
    private final PropertyPhotoService photoService;
    //convert Input DTO -> Entity
    public Unit convertToUnit(UnitInputDTO unitInputDTO, Property property) {
        Unit unit = new Unit();
        unit.setType(unitInputDTO.getType());
        unit.setAreaSqFt(unitInputDTO.getAreaSqFt());
        unit.setFloor(unitInputDTO.getFloor());
        unit.setRentAmount(unitInputDTO.getRentAmount());
        unit.setDepositAmount(unitInputDTO.getDepositAmount());
        unit.setAvailableFrom(unitInputDTO.getAvailableFrom());
        unit.setProperty(property);
        unit.setStatus(unitInputDTO.getStatus());
        unit.setCreatedAt(LocalDate.now());

        return unit;
    }

    public UnitOutputDTO convertToUnitOutputDTO(Unit unit) {

        UnitOutputDTO response = new UnitOutputDTO();

        response.setUnitId(unit.getUnitId());
        response.setType(unit.getType());
        response.setAreaSqFt(unit.getAreaSqFt());
        response.setFloor(unit.getFloor());
        response.setRentAmount(unit.getRentAmount());
        response.setDepositAmount(unit.getDepositAmount());
        response.setAvailableFrom(unit.getAvailableFrom());
        response.setCreatedAt(unit.getCreatedAt());
        response.setStatus(
                unit.getStatus() == null
                        ? UnitStatus.AVAILABLE
                        : unit.getStatus()
        );
        response.setUpdatedAt(unit.getUpdatedAt());

        if (unit.getProperty() != null) {
            response.setPropertyId(unit.getProperty().getPropertyId());
            response.setPropertyName(unit.getProperty().getPropertyName());
            response.setPropertyCity(unit.getProperty().getPropertyCity());
            response.setPropertyState(unit.getProperty().getPropertyState());
            response.setPropertyPostalCode(unit.getProperty().getPropertyPostalCode());
            response.setPropertyCountry(unit.getProperty().getPropertyCountry());
        }

        List<AmenityOutputDTO> amenitiesOutput =
                amenityService.getAmenitiesByUnit(unit.getUnitId());

        List<String> amenities = amenitiesOutput.stream()
                .map(AmenityOutputDTO::getName)
                .toList();

        response.setAmenities(amenities);

        // Set photos
        HashMap<Integer, String> photos =
                photoService.photosByUnit(unit.getUnitId());

        response.setPropertyPhotos(photos);

        return response;
    }

}

