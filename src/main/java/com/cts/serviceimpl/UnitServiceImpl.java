package com.cts.serviceimpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.cts.exception.UnitIdNotFoundException;
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
    public List<UnitOutputDTO> filterUnits(
            String type,
            Double minRent,
            Double maxRent,
            Integer propertyId,
            String propertyName,
            String city,
            String status) {

        List<Unit> units = unitRepository.findAll();

        return units.stream()

                .filter(unit -> type == null ||
                        unit.getType().equalsIgnoreCase(type))

                .filter(unit -> propertyId == null ||
                        unit.getProperty().getPropertyId() == propertyId)

                .filter(unit -> propertyName == null ||
                        unit.getProperty().getPropertyName()
                                .equalsIgnoreCase(propertyName))

                .filter(unit -> city == null ||
                        unit.getProperty().getPropertyCity()
                                .equalsIgnoreCase(city))

                .filter(unit -> status == null ||
                        unit.getStatus().name()
                                .equalsIgnoreCase(status))

                .filter(unit -> minRent == null ||
                        unit.getRentAmount() >= minRent)

                .filter(unit -> maxRent == null ||
                        unit.getRentAmount() <= maxRent)

                .map(unitMapper::convertToUnitOutputDTO)
                .toList();
    }

    @Override
    public UnitOutputDTO updateUnit(int unitId, UnitInputDTO dto) {

        Unit unit = unitRepository.findById(unitId)
                .orElseThrow(() ->
                        new UnitIdNotFoundException("Unit not found with id: " + unitId));

        Property property = propertyRepository.findById(dto.getPropertyId())
                .orElseThrow(() ->
                        new PropertyIdNotFoundException("Property not found with id: " + dto.getPropertyId()));

        unit.setType(dto.getType());
        unit.setAreaSqFt(dto.getAreaSqFt());
        unit.setFloor(dto.getFloor());
        unit.setRentAmount(dto.getRentAmount());
        unit.setDepositAmount(dto.getDepositAmount());
        unit.setAvailableFrom(dto.getAvailableFrom());

        if (dto.getStatus() != null) {
            unit.setStatus(dto.getStatus());
        }

        unit.setProperty(property);
        unit.setUpdatedAt(LocalDate.now());

        Unit updatedUnit = unitRepository.save(unit);

        return unitMapper.convertToUnitOutputDTO(updatedUnit);
    }

}
