package com.cts.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.cts.dto.UnitInputDTO;
import com.cts.dto.UnitOutputDTO;
import com.cts.service.UnitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/unit")
@Tag(name = "Unit Controller", description = "Operations related to Unit management for rental purposes")
public class UnitController {

    private final UnitService unitService;
    
    @PostMapping("/register")
    @PreAuthorize("hasRole('OWNER')")
    @Operation(summary = "Add Unit using required information",
            description = "Returns added unit info, if successfully added.")
    public ResponseEntity<?> addUnit(@RequestBody UnitInputDTO unitDTO) {
        UnitOutputDTO response = unitService.addUnit(unitDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);

    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ADMIN','TENANT','OWNER')")
    @Operation(summary = "Provide the list of all units",
            description = "This will display all the units registered by property owner")
    public ResponseEntity<?> getAllUnit() {
        List<UnitOutputDTO> unit = unitService.findAllUnit();
        return new ResponseEntity<>(unit, HttpStatus.OK);

    }

    @GetMapping("/filter")
    @PreAuthorize("hasAnyRole('ADMIN','TENANT','OWNER')")
    @Operation(
            summary = "Filter units",
            description = "Filter units using optional parameters like type, rent range, propertyId, propertyName, city and status"
    )
    public ResponseEntity<?> filterUnits(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Double minRent,
            @RequestParam(required = false) Double maxRent,
            @RequestParam(required = false) Integer propertyId,
            @RequestParam(required = false) String propertyName,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String status) {

            List<UnitOutputDTO> response = unitService.filterUnits(
                type,
                minRent,
                maxRent,
                propertyId,
                propertyName,
                city,
                status);

        return ResponseEntity.ok(response);
    }
    @PutMapping("/{unitId}")
    @PreAuthorize("hasRole('OWNER')")
    @Operation(
            summary = "Update Unit Details",
            description = "Allows owner to update unit information"
    )
    public ResponseEntity<?> updateUnit(
            @PathVariable int unitId,
            @RequestBody UnitInputDTO unitDTO) {

        UnitOutputDTO response = unitService.updateUnit(unitId, unitDTO);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}

