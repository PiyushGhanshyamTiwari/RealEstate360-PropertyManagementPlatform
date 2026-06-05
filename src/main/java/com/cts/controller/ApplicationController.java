package com.cts.controller;


import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cts.dto.ApplicationInputDTO;
import com.cts.dto.ApplicationOutputDTO;
import com.cts.service.ApplicationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/application")
@Tag(description = "All operations related to Application", name = "Application Controller")
public class ApplicationController {
	private ApplicationService applicationService;
	@Operation(summary = "Submit the application for leasing")
	@PostMapping("/application")
	public ResponseEntity<?> submitApplication( @RequestBody ApplicationInputDTO input){
		ApplicationOutputDTO response = applicationService.submitApplication(input);
		return new ResponseEntity<>(response, HttpStatus.CREATED);
		
	}
	@Operation(summary = "Return List of application by unit Id")
	@GetMapping("/unitId/{unitId}")
	public ResponseEntity<?> getApplicationsByUnitId(@PathVariable int unitId){
		List<ApplicationOutputDTO> response = applicationService.getApplicationsByUnitId(unitId);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	@Operation(summary = "Update status of application")
	@PutMapping("/{applicationId}/{status}")
	public ResponseEntity<?> updateStatusOfApplication(@PathVariable int applicationId,@PathVariable String status){
		ApplicationOutputDTO response = applicationService.updateStatusOfApplication(applicationId,status);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
}
