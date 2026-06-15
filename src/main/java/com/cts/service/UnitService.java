package com.cts.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.cts.dto.UnitInputDTO;
import com.cts.dto.UnitOutputDTO;
import com.cts.entity.Unit;
import com.cts.repository.UnitRepository;

public interface UnitService {


	UnitOutputDTO addUnit(UnitInputDTO unitDTO);

	List<UnitOutputDTO> getAllUnit();

	List<UnitOutputDTO> getUnitByType(String type);

	List<UnitOutputDTO> getUnitByAreaSqFt(double areaSqFt);

	List<UnitOutputDTO> getUnitByFloor(int floor);

	List<UnitOutputDTO> findUnitByRentAmountBetween(double min, double max);

	//List<UnitOutputDTO> findUnitByPropertyId(int propertyId);
	
	List<UnitOutputDTO> findUnitByPropertyId(int propertyId);
}
