package com.cts.controller;

import java.util.List;

import org.apache.catalina.connector.Response;
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

import com.cts.dto.PropertyInputDTO;
import com.cts.dto.PropertyOutputDTO;
import com.cts.entity.Property;
import com.cts.repository.PropertyRepository;
import com.cts.service.PropertyService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/property")
@AllArgsConstructor
@Tag(name= "Property Controller", description="Operations related to property management.")
public class PropertyController {
	private final PropertyService propertyService;
	private final PropertyRepository propertyRepository;
	
	@PostMapping("/register/{ownerId}")
	@PreAuthorize("hasRole('OWNER')")
	@Operation(summary = "Add property using required information",
               description = "Returns added property info, if successfully added. ")
	public ResponseEntity<?> addProperty(@RequestBody PropertyInputDTO propertyInputDTO, @PathVariable int ownerId){
		PropertyOutputDTO response = propertyService.addProperty(propertyInputDTO, ownerId);
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}
	@GetMapping("/all")
	@Operation(summary = "Provide property list which have been added",
               description = "This will display a list of all the registered properties.")
	@PreAuthorize("hasAnyRole('TENANT','OWNER')")
	public ResponseEntity<?> getAllProperties(){
		List<PropertyOutputDTO> property = propertyService.getAllProperties();
		return new ResponseEntity<>(property, HttpStatus.OK);
	}

	@GetMapping("/city/{city}")
	@Operation(summary = "Provide property list for the particular searched type",
               description = "This will display list of property by applying search by city filter")
	@PreAuthorize("hasAnyRole('TENANT','OWNER')")
	public ResponseEntity<?> getPropertyByCity(@PathVariable("city") String propertyCity){
		List<PropertyOutputDTO> property = propertyService.getPropertyByCity(propertyCity);
		return new ResponseEntity<>(property, HttpStatus.OK);
	}
	
	@GetMapping("/state/{state}")
	@Operation(summary = "Provide property list for the particular searched type",
               description = "This will display list of property by applying search by state filter")
	@PreAuthorize("hasAnyRole('TENANT','OWNER')")
	public ResponseEntity<?> getPropertyByState(@PathVariable("state") String propertyState){
		List<PropertyOutputDTO> property = propertyService.getPropertyByState(propertyState);
		return new ResponseEntity<>(property, HttpStatus.OK);
	}
	
	@GetMapping("/ownerid/{ownerid}") 
	@Operation(summary = "Provide property list for the particular searched type",
               description = "This will display list of property by applying search by ownerID filter")  
	@PreAuthorize("hasAnyRole('TENANT','OWNER')")
	public ResponseEntity<?> getPropertyByOwnerId(@PathVariable("ownerid") int ownerId){
		List<PropertyOutputDTO> property = propertyService.getPropertyByOwnerId(ownerId);
		return new ResponseEntity<>(property, HttpStatus.OK);
	}
}
