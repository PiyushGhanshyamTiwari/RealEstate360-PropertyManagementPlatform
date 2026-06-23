package com.cts.mapper;

import java.time.LocalDate;

import com.cts.enums.UnitStatus;
import org.springframework.stereotype.Component;

import com.cts.dto.UnitInputDTO;
import com.cts.dto.UnitOutputDTO;
import com.cts.entity.Property;
import com.cts.entity.Unit;

@Component
public class UnitMapper {

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

    //convert Entity -> OutputDTO
    public UnitOutputDTO convertToUnitOutputDTO(Unit unit) {
        UnitOutputDTO response= new UnitOutputDTO();
        response.setUnitId(unit.getUnitId());
        response.setType(unit.getType());
        response.setAreaSqFt(unit.getAreaSqFt());
        response.setFloor(unit.getFloor());
        response.setRentAmount(unit.getRentAmount());
        response.setDepositAmount(unit.getDepositAmount());
        response.setAvailableFrom(unit.getAvailableFrom());
        response.setCreatedAt(unit.getCreatedAt());
        response.setStatus(UnitStatus.valueOf(unit.getStatus().name()));
        response.setUpdatedAt(unit.getUpdatedAt());
        response.setPropertyId(unit.getProperty() != null ? unit.getProperty().getPropertyId() : 0);
        return response;
    }

}

