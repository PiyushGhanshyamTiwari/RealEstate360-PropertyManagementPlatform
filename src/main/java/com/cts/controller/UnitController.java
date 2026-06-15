package com.cts.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cts.dto.UnitInputDTO;
import com.cts.dto.UnitOutputDTO;
import com.cts.entity.Unit;
import com.cts.repository.UnitRepository;
import com.cts.service.UnitService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/unit")
@Tag(name= "Unit Controller", description="Operations related to Unit management for rental purposes")
public class UnitController {
	private final UnitService unitService;

	
	@PostMapping("/register")
	@PreAuthorize("hasRole('OWNER')")
	@Operation(summary = "Add Unit using required information",
               description = "Returns added unit info, if successfully added. ")
	public ResponseEntity<?> addUnit(@RequestBody UnitInputDTO unitDTO){
		UnitOutputDTO response = unitService.addUnit(unitDTO);
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}
	
	@GetMapping("/all")
	@PreAuthorize("hasAnyRole('ADMIN','TENANT','OWNER')")
	@Operation(summary = "Provide the list of all units",
               description = "This will display all the units registered by property owner")
	public ResponseEntity<?> getAllUnit(){
		List<UnitOutputDTO> unit = unitService.getAllUnit();
		return new ResponseEntity<>(unit, HttpStatus.OK);
	}
	
	@GetMapping("/type/{type}")
	@PreAuthorize("hasAnyRole('ADMIN','TENANT','OWNER')")
	@Operation(summary = "Provide unit list for the particular searched type",
               description = "This will display list of unit by applying search by type filter")
	public ResponseEntity<?> getUnitByType(@PathVariable String type){
		List<UnitOutputDTO> unit= unitService.getUnitByType(type);
		return new ResponseEntity<>(unit, HttpStatus.OK);
	}
	
	@GetMapping("areaSqFt/{areaSqFt}")
	@PreAuthorize("hasAnyRole('ADMIN','TENANT','OWNER')")
	@Operation(summary = "Provide unit list for the particular searched type",
               description = "This will display list of unit by applying search by area square feet filter")
	public ResponseEntity<?> getUnitByAreaSqFt(@PathVariable double areaSqFt){
		List<UnitOutputDTO> unit= unitService.getUnitByAreaSqFt(areaSqFt);
		return new ResponseEntity<>(unit, HttpStatus.OK);
	}

	@GetMapping("floor/{floor}")
	@PreAuthorize("hasAnyRole('ADMIN','TENANT','OWNER')")
	@Operation(summary = "Provide unit list for the particular searched type",
    description = "This will display list of unit by applying search by floor filter")
	public ResponseEntity<?> getUnitByFloor(@PathVariable int floor){
		List<UnitOutputDTO> unit= unitService.getUnitByFloor(floor);
		return new ResponseEntity<>(unit, HttpStatus.OK);
	}
	@GetMapping("rentAmount/{min}/{max}")
	@PreAuthorize("hasAnyRole('ADMIN','TENANT','OWNER')")
	@Operation(summary = "Provide unit list for the particular searched type",
    description = "This will display list of unit by applying search by rent between particular range")
	public ResponseEntity<?> findUnitByRentAmountBetween(@PathVariable double min, @PathVariable double max){
		List<UnitOutputDTO> unit= unitService.findUnitByRentAmountBetween(min,max);
		return new ResponseEntity<>(unit, HttpStatus.OK);
	}
//	@GetMapping("propertyId/{propertyId}")
//	@Operation(summary = "Provide unit list for the particular searched type",
//    description = "This will display list of unit by applying search by property Id filter")
//	public ResponseEntity<?> findUnitByPropertyId(@PathVariable int propertyId){
//		List<UnitOutputDTO> unit= unitService.findUnitByPropertyId(propertyId);
//		return new ResponseEntity<>(unit, HttpStatus.OK);
//	}
	


     @GetMapping("/{propertyId}")
     @PreAuthorize("hasAnyRole('ADMIN','TENANT','OWNER')")
     @Operation(summary = "Provide unit list based on pagination",
                description = "This will display list of unit on specified page")
     public ResponseEntity<?> findUnitByPropertyId(@PathVariable int propertyId) {

    List<UnitOutputDTO> response =
            unitService.findUnitByPropertyId(propertyId);

    return new ResponseEntity<>(response, HttpStatus.OK);
}



}
