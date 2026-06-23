package com.cts.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.cts.dto.UnitInputDTO;
import com.cts.dto.UnitOutputDTO;
import com.cts.entity.Unit;
import com.cts.repository.UnitRepository;

public interface UnitService {


	public UnitOutputDTO addUnit(UnitInputDTO unit);
    public List<UnitOutputDTO> findUnitByType(String type);
    public List<UnitOutputDTO> findUnitByPriceRange(double minPrice, double maxPrice);
    public List<UnitOutputDTO> findUnitByPropertyId(int propertyId);
    public List<UnitOutputDTO> findAllUnit();
    public List<UnitOutputDTO> findUnitByFloor(int floor);
    public List<UnitOutputDTO> findUnitByAreaSqFt(double areaSqFt);
    public List<UnitOutputDTO> findUnitByStatus(String status);
}