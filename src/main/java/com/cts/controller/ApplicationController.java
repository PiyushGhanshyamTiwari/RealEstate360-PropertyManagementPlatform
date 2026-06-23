package com.cts.controller;


import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
	@PreAuthorize("hasAnyRole('TENANT','OWNER')")
	public ResponseEntity<?> submitApplication( @RequestBody ApplicationInputDTO input){
		ApplicationOutputDTO response = applicationService.submitApplication(input);
		return new ResponseEntity<>(response, HttpStatus.CREATED);
		
	}
	@Operation(summary = "Return List of application by unit Id")
	@GetMapping("/unitId/{unitId}")
    @PreAuthorize("hasAnyRole('ADMIN','TENANT','OWNER')")
	public ResponseEntity<?> getApplicationsByUnitId(@PathVariable int unitId){
		List<ApplicationOutputDTO> response = applicationService.getApplicationsByUnitId(unitId);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	@Operation(summary = "Update status of application")
	@PutMapping("/{applicationId}/{status}")
	@PreAuthorize("hasRole('OWNER')")
	public ResponseEntity<?> updateStatusOfApplication(@PathVariable int applicationId,@PathVariable String status){
		ApplicationOutputDTO response = applicationService.updateStatusOfApplication(applicationId,status);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

    @Operation(summary = "Get List of Application by User Id")
    @GetMapping("/userId/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN','TENANT')")
    public ResponseEntity<?> getApplicationByTenantId(@PathVariable int userId){
         List<ApplicationOutputDTO> list = applicationService.getApplicationByTenantId(userId);
         return new ResponseEntity<>(list,HttpStatus.OK);
    }

    @Operation(summary = "Get List of Application by Application Id")
    @GetMapping("/applicationId/{applicationId}")
    @PreAuthorize("hasAnyRole('ADMIN','TENANT')")
    public ResponseEntity<?> getApplicationByApplicationId(@PathVariable int applicationId){
        ApplicationOutputDTO response = applicationService.getApplicationByApplicationId(applicationId);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
}
