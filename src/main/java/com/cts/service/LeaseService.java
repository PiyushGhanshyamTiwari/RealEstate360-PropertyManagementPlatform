package com.cts.service;

import java.util.List;

import com.cts.dto.LeaseInputDTO;
import com.cts.dto.LeaseOutputDTO;

public interface LeaseService {

	public LeaseOutputDTO leaseGeneration(LeaseInputDTO input);

	public LeaseOutputDTO updateLeaseStatus(int leaseId, String status);

	/** Returns all leases in the system (ADMIN / ACCOUNT OFFICER) */
	public List<LeaseOutputDTO> getAllLeases();

	/** Returns leases for all units owned by the given owner userId (OWNER) */
	public List<LeaseOutputDTO> getLeasesByOwnerUserId(int ownerUserId);

	/** Returns leases associated with the given tenant profile ID (TENANT) */
	public List<LeaseOutputDTO> getLeasesByTenantId(int tenantId);

}