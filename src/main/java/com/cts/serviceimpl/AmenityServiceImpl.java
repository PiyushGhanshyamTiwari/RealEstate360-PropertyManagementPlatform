package com.cts.serviceimpl;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.cts.annotation.Audit;
import com.cts.constants.AuditActions;
import com.cts.dto.AmenityInputDTO;
import com.cts.dto.AmenityOutputDTO;
import com.cts.entity.Amenity;
import com.cts.entity.Unit;
import com.cts.repository.AmenityRepository;
import com.cts.repository.UnitRepository;
import com.cts.service.AmenityService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AmenityServiceImpl implements AmenityService {

    private final AmenityRepository amenityRepository;
    private final UnitRepository unitRepository;

    @Override
    @Audit(action = AuditActions.CREATE_AMENITY, resourceType = "Amenity")
    public AmenityOutputDTO addAmenity(AmenityInputDTO amenityInputDto, int unitId) {
        Unit unit = unitRepository.findById(unitId).orElse(null);
        Amenity amenity = new Amenity();
        amenity.setUnit(unit);
        amenity.setName(amenityInputDto.getName());
        amenity.setDescription(amenityInputDto.getDescription());
        amenity.setCreatedAt(LocalDate.now());
        amenity = amenityRepository.save(amenity);
        return toAmenityOutputDto(amenity);
    }

    @Override
    public List<AmenityOutputDTO> getAllAmenities() {
        return amenityRepository.findAll()
                .stream()
                .map(this::toAmenityOutputDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<AmenityOutputDTO> getAmenitiesByUnit(int unitId) {
        return amenityRepository.findByUnitId(unitId)
                .stream()
                .map(this::toAmenityOutputDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<AmenityOutputDTO> getAmenitiesByName(String name) {
        return amenityRepository.findByName(name)
                .stream()
                .map(this::toAmenityOutputDto)
                .collect(Collectors.toList());
    }

    private AmenityOutputDTO toAmenityOutputDto(Amenity amenity) {
        return AmenityOutputDTO.builder()
                .amenityId(amenity.getAmenityId())
                .unitId(amenity.getUnit().getUnitId())
                .name(amenity.getName())
                .description(amenity.getDescription())
                .createdAt(amenity.getCreatedAt())
                .type(amenity.getUnit().getType())
                .areaSqFt(amenity.getUnit().getAreaSqFt())
                .floor(amenity.getUnit().getFloor())
                .rentAmount(amenity.getUnit().getRentAmount())
                .depositAmount(amenity.getUnit().getDepositAmount())
                .build();
    }
}
