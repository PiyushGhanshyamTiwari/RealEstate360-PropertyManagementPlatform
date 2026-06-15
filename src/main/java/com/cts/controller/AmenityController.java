package com.cts.controller;
 
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.cts.dto.AmenityInputDTO;
import com.cts.dto.AmenityOutputDTO;
import com.cts.service.AmenityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
 
@RestController
@AllArgsConstructor
@RequestMapping("api/v1/amenity")
@Tag(name = "Amenity Controller", description = "Operations related to Amenities")
public class AmenityController {
    private final AmenityService amenityService;
 
    @PostMapping("/register/{unitId}")
    @Operation(summary = "Insert amenity", description = "Post amenity details if registered successfully")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<?> addAmenity(@RequestBody AmenityInputDTO amenityInputDto, @PathVariable int unitId) {
        AmenityOutputDTO response = amenityService.addAmenity(amenityInputDto, unitId);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
 
    @GetMapping("/all")
    @Operation(summary = "Get all amenities", description = "Return all amenities details")
    @PreAuthorize("hasAnyRole('TENANT','OWNER')")
    public ResponseEntity<?> getAllAmenities() {
        List<AmenityOutputDTO> amenities = amenityService.getAllAmenities();
        return new ResponseEntity<>(amenities, HttpStatus.OK);
    }
 
    @GetMapping("/unit/{unitId}")
    @Operation(summary = "Get amenities by unit", description = "Returns amenities based on unit id")
    @PreAuthorize("hasAnyRole('TENANT','OWNER')")
    public ResponseEntity<?> getAmenitiesByUnit(@PathVariable int unitId) {
        List<AmenityOutputDTO> amenities = amenityService.getAmenitiesByUnit(unitId);
        return new ResponseEntity<>(amenities, HttpStatus.OK);
    }
 
    @GetMapping("/name/{name}")
    @Operation(summary = "Get amenities by name", description = "Returns amenities based on name")
    @PreAuthorize("hasAnyRole('TENANT','OWNER')")
    public ResponseEntity<?> getAmenitiesByName(@PathVariable String name) {
        List<AmenityOutputDTO> amenities = amenityService.getAmenitiesByName(name);
        return new ResponseEntity<>(amenities, HttpStatus.OK);
    }
}