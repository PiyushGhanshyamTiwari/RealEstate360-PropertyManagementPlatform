package com.cts.service;

import java.util.List;

import com.cts.dto.TenantProfileInputDTO;
import com.cts.dto.TenantProfileOutputDTO;



public interface TenantProfileService {

	public TenantProfileOutputDTO addTenant(TenantProfileInputDTO input);

	public List<TenantProfileOutputDTO> getAllTenants();

	public TenantProfileOutputDTO getTenantById(int tenantId);

	public TenantProfileOutputDTO getTenantByUserId(int userId);

}
