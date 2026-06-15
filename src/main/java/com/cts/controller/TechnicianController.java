package com.cts.controller;

import com.cts.dto.*;
import com.cts.service.*;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/technicians")
@AllArgsConstructor
@Tag(name = "Technician Controller", description = "Operation related to Technicians")
public class TechnicianController {

    private final TechnicianService technicianService;

    @PostMapping
    @Operation(summary = "Create Technician")
    @PreAuthorize("hasRole('TECHNICIAN')")
    public ResponseEntity<TechnicianOutputDTO> createTechnician(
            @RequestBody @Valid TechnicianInputDTO technicianInputDTO) {

        TechnicianOutputDTO response = technicianService.createTechnician(technicianInputDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{technicianid}")
    @Operation(summary = "Get technician by technicianId",
            description = "Returns technician based on technician id")
    @PreAuthorize("hasAnyRole('ADMIN','TENANT','OWNER')")
    public ResponseEntity<TechnicianOutputDTO> getTechnicianById(
            @PathVariable("technicianid") int technicianId) {

        TechnicianOutputDTO response = technicianService.getTechnicianById(technicianId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping
    @Operation(summary = "Get all Technicians",
            description = "Returns paginated technician list")
    @PreAuthorize("hasAnyRole('ADMIN','TENANT','OWNER')")
    public ResponseEntity<List<TechnicianOutputDTO>> getAllTechnicians(
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "5") int pageSize) {

        List<TechnicianOutputDTO> response =
                technicianService.getAllTechnicians(pageNo, pageSize);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/{technicianId}")
    @Operation(summary = "Update Technicians",
            description = "Updates technician details")
    @PreAuthorize("hasAnyRole('ADMIN', 'TECHNICIAN')")
    public ResponseEntity<TechnicianOutputDTO> updateTechnician(
            @PathVariable("technicianId") int technicianId,
            @Valid @RequestBody TechnicianInputDTO inputDTO) {

        TechnicianOutputDTO response =
                technicianService.updateTechnician(technicianId, inputDTO);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PatchMapping("/{technicianId}/deactivate")
    @Operation(summary = "Deactivate Technicians",
            description = "Marks technician as inactive")
    @PreAuthorize("hasRole('ADMIN','TECHNICIAN')")
    public ResponseEntity<String> deactivateTechnician(
            @PathVariable("technicianId") int technicianId) {

        technicianService.deactivateTechnician(technicianId);
        return new ResponseEntity<>("Technician deactivated successfully", HttpStatus.OK);
    }

    @GetMapping("/available")
    @Operation(summary = "Get available Technicians",
            description = "Returns all available technicians")
    @PreAuthorize("hasAnyRole('ADMIN','TENANT','OWNER')")
    public ResponseEntity<?> getAvailableTechnicians() {

        List<TechnicianOutputDTO> response = technicianService.getAvailableTechnicians();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/city/{city}")
    @Operation(summary = "Get technician by city",
            description = "Returns technician based on city")
    @PreAuthorize("hasAnyRole('ADMIN','TENANT','OWNER')")
    public ResponseEntity<?> getTechnicianByCity(
            @PathVariable String city) {

        List<TechnicianOutputDTO> response =
                technicianService.getTechnicianByCity(city);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/specialization/{specialization}")
    @Operation(summary = "Get technician by specialization",
            description = "Returns technician based on specialization")
    @PreAuthorize("hasAnyRole('ADMIN','TENANT','OWNER')")
    public ResponseEntity<?> getTechnicianBySpecialization(
            @PathVariable String specialization) {

        List<TechnicianOutputDTO> response =
                technicianService.getTechnicianBySpecialiaztion(specialization);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}