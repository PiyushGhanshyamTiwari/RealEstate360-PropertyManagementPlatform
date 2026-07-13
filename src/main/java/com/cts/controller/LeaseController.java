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

import com.cts.dto.LeaseInputDTO;
import com.cts.dto.LeaseOutputDTO;
import com.cts.service.LeaseService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/lease")
@Tag(description = "All operations related to lease", name = "Lease Controller")
public class LeaseController {
	private LeaseService leaseService;
	
	@Operation(summary = "Generating the lease aggrement")
	@PutMapping("/{leaseId}/{status}")
	@PreAuthorize("hasRole('OWNER')")
	public ResponseEntity<?> updateLeaseStatus(@PathVariable int leaseId,@PathVariable String status){
		LeaseOutputDTO response = leaseService.updateLeaseStatus(leaseId,status);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@Operation(summary = "Listing all leases")
	@GetMapping("/all")
	@PreAuthorize("hasAnyRole('ACCOUNT OFFICER', 'ADMIN')")
	public ResponseEntity<List<LeaseOutputDTO>> listAllLeases() {
		List<LeaseOutputDTO> response = leaseService.getAllLeases();
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@Operation(summary = "Listing leases for an owner")
	@GetMapping("/ownerUserId/{ownerUserId}")
	@PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
	public ResponseEntity<List<LeaseOutputDTO>> listLeasesByOwnerUserId(@PathVariable int ownerUserId) {
		List<LeaseOutputDTO> response = leaseService.getLeasesByOwnerUserId(ownerUserId);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@Operation(summary = "Listing leases for a tenant")
	@GetMapping("/tenantId/{tenantId}")
	@PreAuthorize("hasAnyRole('TENANT', 'ADMIN', 'ACCOUNT OFFICER')")
	public ResponseEntity<List<LeaseOutputDTO>> listLeasesByTenantId(@PathVariable int tenantId) {
		List<LeaseOutputDTO> response = leaseService.getLeasesByTenantId(tenantId);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
}

