package com.cts.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.cts.dto.UnitInputDTO;
import com.cts.dto.UnitOutputDTO;
import com.cts.entity.Unit;
import com.cts.repository.UnitRepository;

public interface UnitService {


	public UnitOutputDTO addUnit(UnitInputDTO unit);
    public List<UnitOutputDTO> findAllUnit();
    public List<UnitOutputDTO> filterUnits(
            String type,
            Double minRent,
            Double maxRent,
            Integer propertyId,
            String propertyName,
            String city,
            String status);
    public UnitOutputDTO updateUnit(int unitId, UnitInputDTO unitDTO);
}