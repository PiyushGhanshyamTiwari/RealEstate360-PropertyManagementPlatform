package com.cts.serviceimpl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.cts.annotation.Audit;
import com.cts.constants.AuditActions;
import com.cts.dto.UnitInputDTO;
import com.cts.dto.UnitOutputDTO;
import com.cts.entity.Property;
import com.cts.entity.Unit;
import com.cts.enums.UnitStatus;
import com.cts.exception.PropertyIdNotFoundException;
import com.cts.mapper.UnitMapper;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;

import com.cts.repository.PropertyRepository;
import com.cts.repository.UnitRepository;
import com.cts.service.UnitService;

import com.cts.entity.User;
import com.cts.exception.UserIdNotFoundException;
import com.cts.repository.UserRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UnitServiceImpl implements UnitService {

    private final UnitRepository unitRepository;
    private final PropertyRepository propertyRepository;
    private final UnitMapper unitMapper;
    private final UserRepository userRepository;

    @Override
    @Audit(action = AuditActions.CREATE_UNIT, resourceType = "Unit")
    public UnitOutputDTO addUnit(UnitInputDTO unitInputDTO) {

        Property property = propertyRepository.findById(unitInputDTO.getPropertyId())
                .orElseThrow(() ->
                        new PropertyIdNotFoundException("Property Id not found"));

        // Logged-in user
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User loggedInUser = userRepository.findUserByEmail(email);

        if (loggedInUser == null) {
            throw new UserIdNotFoundException("User not found");
        }

        // Verify property ownership
        if (!property.getUser().getUserId().equals(loggedInUser.getUserId())) {
            throw new AccessDeniedException(
                    "Only the owner of this property can add units");
        }

        Unit unit = unitMapper.convertToUnit(unitInputDTO, property);

        unit = unitRepository.save(unit);

        return unitMapper.convertToUnitOutputDTO(unit);
    }
    
    @Override
    public List<UnitOutputDTO> findAllUnit() {

        return unitRepository.findAll()

                .stream()

                .map(unit -> unitMapper.convertToUnitOutputDTO(unit))

                .collect(Collectors.toList());

    }

    @Override
    public List<UnitOutputDTO> findUnitByType(String type) {

        return unitRepository.findUnitByType(type)

                .stream()

                .map(unit -> unitMapper.convertToUnitOutputDTO(unit))

                .collect(Collectors.toList());

    }

    @Override
    public List<UnitOutputDTO> findUnitByAreaSqFt(double areaSqFt) {

        return unitRepository.findUnitByAreaSqFt(areaSqFt)

                .stream()

                .map(unit -> unitMapper.convertToUnitOutputDTO(unit))

                .collect(Collectors.toList());

    }
    
    @Override
    public List<UnitOutputDTO> findUnitByFloor(int floor) {

        return unitRepository.findUnitByFloor(floor)

                .stream()

                .map(unit -> unitMapper.convertToUnitOutputDTO(unit))

                .collect(Collectors.toList());

    }

    @Override
    public List<UnitOutputDTO> findUnitByPriceRange(double min, double max) {

        return unitRepository.findUnitByPriceRange(min, max)

                .stream()

                .map(unit -> unitMapper.convertToUnitOutputDTO(unit))

                .collect(Collectors.toList());

    }

    @Override
    public List<UnitOutputDTO> findUnitByPropertyId(int propertyId) {
        return unitRepository.findUnitByPropertyId(propertyId)
                .stream()
                .map(unit -> unitMapper.convertToUnitOutputDTO(unit))
                .collect(Collectors.toList());
    }
    
    public List<UnitOutputDTO> findUnitByStatus(String status) {

        UnitStatus unitStatus = UnitStatus.valueOf(status.toUpperCase());

        return unitRepository.findByStatus(unitStatus)
                .stream()
                .map(unit -> unitMapper.convertToUnitOutputDTO(unit))
                .collect(Collectors.toList());
    }

}
