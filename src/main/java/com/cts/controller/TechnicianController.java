package com.cts.controller;

import com.cts.dto.*;

import com.cts.service.*;

import java.util.List;

import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.security.core.Authentication;

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
    @PreAuthorize("hasAnyRole('TECHNICIAN')")
    public ResponseEntity<TechnicianOutputDTO> createTechnician(
            @RequestBody @Valid TechnicianInputDTO technicianInputDTO) {
        TechnicianOutputDTO response = technicianService.createTechnician(technicianInputDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);

    }

    @GetMapping("/{userid}")
    @Operation(summary = "Get technician by userid",
            description = "Returns technician based on userid ")
    @PreAuthorize("hasAnyRole('ADMIN','OWNER','TECHNICIAN')")
    public ResponseEntity<TechnicianOutputDTO> getTechnicianById(
            @PathVariable("userid") int userid) {
        TechnicianOutputDTO response = technicianService.getTechnicianById(userid);
        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    @GetMapping
    @Operation(summary = "Get all Technicians",
            description = "Returns all technicians")
    @PreAuthorize("hasAnyRole('ADMIN','OWNER')")
    public ResponseEntity<List<TechnicianOutputDTO>> getAllTechnicians() {
        List<TechnicianOutputDTO> response = technicianService.getAllTechnicians();
        return new ResponseEntity<>(response, HttpStatus.OK);

    }

//SERVICE, SPECIAL
    @GetMapping("/search")
    @Operation(summary = "Search technicians",
            description = "Filter by specialization and city")
    @PreAuthorize("hasAnyRole('ADMIN','TENANT','OWNER')")
    public ResponseEntity<List<TechnicianOutputDTO>> searchTechnicians(
            @RequestParam String specialization,
            @RequestParam String city) {
        List<TechnicianOutputDTO> response =
                technicianService.getTechnicianBySpecializationAndCity(specialization, city);
        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    @GetMapping("/{userid}/getAllSchedules")
    @Operation(summary = "Get technician's all schedules")
    @PreAuthorize("hasAnyRole('TECHNICIAN','OWNER','ADMIN')")
    public ResponseEntity<List<MaintenanceScheduleResponseDTO>> getWorkHistoryById(
            @PathVariable int userid) {
        List<MaintenanceScheduleResponseDTO> response =
                technicianService.getWorkHistory(userid);
        return new ResponseEntity<>(response, HttpStatus.OK);

    }

}
