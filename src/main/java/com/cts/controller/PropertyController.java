package com.cts.controller;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.cts.dto.PropertyInputDTO;
import com.cts.dto.PropertyOutputDTO;
import com.cts.service.PropertyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;


@RestController
@RequestMapping("/api/v1/property")
@AllArgsConstructor
@Tag(name = "Property Controller", description = "Operations related to property management.")
public class PropertyController {

    private final PropertyService propertyService;

    @PostMapping("/register/{ownerId}")
    @PreAuthorize("hasRole('OWNER')")
    @Operation(summary = "Add property using required information",
            description = "Returns added property info, if successfully added.")
    public ResponseEntity<?> addProperty(@RequestBody PropertyInputDTO propertyInputDTO, @PathVariable int ownerId) {
        PropertyOutputDTO response = propertyService.addProperty(propertyInputDTO, ownerId);
        return new ResponseEntity<>(response, HttpStatus.CREATED);

    }

    @GetMapping("/city/{city}")
    @PreAuthorize("hasAnyRole('OWNER','TENANT','ADMIN')")
    @Operation(summary = "Provide property list for the particular searched type",
            description = "This will display list of property by applying search by city filter")
    public ResponseEntity<?> findPropertyByCity(@PathVariable("city") String propertyCity) {
        List<PropertyOutputDTO> property = propertyService.findPropertyByCity(propertyCity);
        return new ResponseEntity<>(property, HttpStatus.OK);

    }

    @GetMapping("/state/{state}")
    @PreAuthorize("hasAnyRole('OWNER','TENANT','ADMIN')")
    @Operation(summary = "Provide property list for the particular searched type",
            description = "This will display list of property by applying search by state filter")
    public ResponseEntity<?> getPropertyByState(@PathVariable("state") String propertyState){
        List<PropertyOutputDTO> property = propertyService.findPropertyByState(propertyState);
        return new ResponseEntity<>(property, HttpStatus.OK);

    }

    @GetMapping("/ownerId/{ownerId}")
    @PreAuthorize("hasAnyRole('OWNER','TENANT','ADMIN')")
    @Operation(summary = "Provide property list for the particular searched type",
            description = "This will display list of property by applying search by ownerID filter")
    public ResponseEntity<?> findPropertyByOwnerId(@PathVariable("ownerId") int ownerId){
        List<PropertyOutputDTO> property = propertyService.findPropertyByOwnerId(ownerId);
        return new ResponseEntity<>(property, HttpStatus.OK);

    }

}

