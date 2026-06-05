package com.cts.controller;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import com.cts.dto.TenantProfileInputDTO;

import com.cts.dto.TenantProfileOutputDTO;

import com.cts.service.TenantProfileService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/tenant")
@Tag(name= "Tenant Profile Controller", description="Operations related to Tenant.")
public class TenantProfileController {

    private final TenantProfileService tenantProfileService;

    public TenantProfileController(TenantProfileService tenantProfileService) {
        this.tenantProfileService = tenantProfileService;
    }

    @Operation(summary = "Register Tenant with File Upload")
    @PreAuthorize("hasRole('TENANT')")
    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> addTenant(
            @RequestBody TenantProfileInputDTO input) {

        return ResponseEntity.ok(tenantProfileService.addTenant(input));
    }
    
    @Operation(summary = "Give all Tenants")
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllTenants(){
    	List<TenantProfileOutputDTO> response = tenantProfileService.getAllTenants();
    	return new ResponseEntity<>(response,HttpStatus.OK);
    }
    
    @Operation(summary = "Give Tenants by tenantId")
    @GetMapping("/tenantId/{tenantId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getTenantById(@PathVariable int tenantId){
    	TenantProfileOutputDTO response = tenantProfileService.getTenantById(tenantId);
    	return new ResponseEntity<>(response,HttpStatus.OK);
    }
    @Operation(summary = "Give Tenants by UserId")
    @GetMapping("/userId/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getTenantByUserId(@PathVariable int userId){
    	TenantProfileOutputDTO response = tenantProfileService.getTenantByUserId(userId);
    	return new ResponseEntity<>(response,HttpStatus.OK);
    }
}