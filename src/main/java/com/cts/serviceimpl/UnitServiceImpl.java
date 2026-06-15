package com.cts.serviceimpl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.cts.dto.UnitInputDTO;
import com.cts.dto.UnitOutputDTO;

import com.cts.entity.Property;
import com.cts.entity.Unit;
import com.cts.mapper.UnitMapper;
import com.cts.repository.PropertyRepository;
import com.cts.repository.UnitRepository;
import com.cts.service.UnitService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UnitServiceImpl implements UnitService {

	private final UnitRepository unitRepository;
	private final PropertyRepository propertyRepository;
	private final UnitMapper unitMapper;
	
	@Override
	public UnitOutputDTO addUnit(UnitInputDTO unitInputDTO) {
		
		
		Property property = propertyRepository.findById(unitInputDTO.getPropertyId()).orElse(null);
		
		Unit unit = unitMapper.convertToUnit(unitInputDTO, property);
		unit = unitRepository.save(unit);
		return unitMapper.convertToUnitOutputDTO(unit);

	}

	@Override
	public List<UnitOutputDTO> getAllUnit() {
		return unitRepository.findAll()
				.stream()
				.map(unit->unitMapper.convertToUnitOutputDTO(unit))
				.collect(Collectors.toList());
	}

	@Override
	public List<UnitOutputDTO> getUnitByType(String type) {
		return unitRepository.findUnitByType(type)
				.stream()
				.map(unit->unitMapper.convertToUnitOutputDTO(unit))
				.collect(Collectors.toList());
	}

	@Override
	public List<UnitOutputDTO> getUnitByAreaSqFt(double areaSqFt) {
		return unitRepository.findUnitByAreaSqFt(areaSqFt)
				.stream()
				.map(unit->unitMapper.convertToUnitOutputDTO(unit))
				.collect(Collectors.toList());
	}

	@Override
	public List<UnitOutputDTO> getUnitByFloor(int floor) {
		return unitRepository.findUnitByFloor(floor)
				.stream()
				.map(unit->unitMapper.convertToUnitOutputDTO(unit))
				.collect(Collectors.toList());
	}

	@Override
	public List<UnitOutputDTO> findUnitByRentAmountBetween(double min, double max) {
		return unitRepository.findUnitByRentAmountBetween(min, max)
				.stream()
				.map(unit->unitMapper.convertToUnitOutputDTO(unit))
				.collect(Collectors.toList());
	}

	//@Override
	//public List<UnitOutputDTO> findUnitByPropertyId(int propertyId) {
		
//		List<Unit> units = unitRepository.findUnitByPropertyPropertyId(propertyId);
//		List<UnitOutputDTO> list = new ArrayList<>();
//		for(Unit savedUnit:units) {
//			UnitOutputDTO output = UnitOutputDTO.builder()
//		            .unitId(savedUnit.getUnitId())
//		            .type(savedUnit.getType())
//		            .areaSqFt(savedUnit.getAreaSqFt())
//		            .floor(savedUnit.getFloor())
//		            .rentAmount(savedUnit.getRentAmount())
//		            .depositAmount(savedUnit.getDepositAmount())
//		            .availableFrom(savedUnit.getAvailableFrom())
//		            .propertyId(savedUnit.getProperty().getPropertyId())
//		            .build();
//			list.add(output);
//		}
		
//	@Override
//	public List<UnitOutputDTO> findUnitByPropertyId(int propertyId, int pageNo, int pageSize) {
//
//         Pageable pageable = PageRequest.of(pageNo, pageSize);
//
//         Page<Unit> unitPage = unitRepository.findUnitByPropertyId(propertyId, pageable);
//
//         List<Unit> units = unitPage.getContent();
//
//         List<UnitOutputDTO> output = new ArrayList<>();
//         
//
//        for (Unit unit : units) {
//
//        Property property = unit.getProperty();  // already mapped
//
//        UnitOutputDTO response = new UnitOutputDTO();
//
//        response.setUnitId(unit.getUnitId());
//        response.setType(unit.getType());
//        response.setAreaSqFt(unit.getAreaSqFt());
//        response.setFloor(unit.getFloor());
//        response.setRentAmount(unit.getRentAmount());
//        response.setDepositAmount(unit.getDepositAmount());
//        response.setAvailableFrom(unit.getAvailableFrom());
//        response.setPropertyId(property.getPropertyId());
//        output.add(response);
//    }

	@Override
    public List<UnitOutputDTO> findUnitByPropertyId(int propertyId) {
        return unitRepository.findUnitByPropertyId(propertyId)
                .stream()
                .map(unit -> unitMapper.convertToUnitOutputDTO(unit))
                .collect(Collectors.toList());
	}

//       if (output.isEmpty()) {
//             throw new RuntimeException("No units found for given propertyId");
//    }
//
//      return output;
//	}


}
	
	


