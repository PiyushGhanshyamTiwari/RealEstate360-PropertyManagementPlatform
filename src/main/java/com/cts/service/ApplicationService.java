package com.cts.service;

import java.util.List;

import com.cts.dto.ApplicationInputDTO;
import com.cts.dto.ApplicationOutputDTO;

public interface ApplicationService {

	public ApplicationOutputDTO submitApplication(ApplicationInputDTO input);

	public List<ApplicationOutputDTO> getApplicationsByUnitId(int unitId);

	public ApplicationOutputDTO updateStatusOfApplication(int applicationId, String status);

    public List<ApplicationOutputDTO> getApplicationByTenantId(int userId);

    public ApplicationOutputDTO getApplicationByApplicationId(int applicationId);
}
