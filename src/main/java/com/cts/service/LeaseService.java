package com.cts.service;

import com.cts.dto.LeaseInputDTO;
import com.cts.dto.LeaseOutputDTO;

public interface LeaseService {

	public LeaseOutputDTO leaseGeneration(LeaseInputDTO input);

	public LeaseOutputDTO updateLeaseStatus(int leaseId, String status);

}