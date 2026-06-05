package com.cts.serviceimpl;

import org.springframework.stereotype.Service;

import com.cts.dto.LeaseInputDTO;
import com.cts.dto.LeaseOutputDTO;
import com.cts.entity.Lease;
import com.cts.entity.TenantProfile;
import com.cts.entity.Unit;
import com.cts.exception.TenantIdNotFoundException;
import com.cts.exception.UnitIdNotFoundException;
import com.cts.mapper.LeaseMapper;
import com.cts.repository.LeaseRepository;
import com.cts.repository.TenantProfileRepository;
import com.cts.repository.UnitRepository;
import com.cts.service.LeaseService;

import lombok.AllArgsConstructor;
@Service
@AllArgsConstructor
public class LeaseServiceImpl implements LeaseService{
	private LeaseRepository leaseRepository;
	private UnitRepository unitRepository;
	private TenantProfileRepository tenantProfileRepository;
	@Override
	public LeaseOutputDTO leaseGeneration(LeaseInputDTO input) {
		// TODO Auto-generated method stub
		Unit unit = unitRepository.findById(input.getUnitId())
				.orElseThrow(()->new UnitIdNotFoundException("Unit Id dosen't exists"));
		
		TenantProfile tenantProfile = tenantProfileRepository.findById(input.getTenantId())
				.orElseThrow(()->new TenantIdNotFoundException("Tenant Id dosen't exists"));
		
		Lease lease = LeaseMapper.convertToLease(input, unit, tenantProfile);
		Lease savedLease = leaseRepository.save(lease);
		
		return LeaseMapper.convertToLeaseOutputDto(savedLease);
	}
	@Override
	public LeaseOutputDTO updateLeaseStatus(int leaseId, String status) {
		// TODO Auto-generated method stub
		Lease lease = leaseRepository.findById(leaseId)
				.orElseThrow(()->new RuntimeException("Lease Id not exists"));
		Lease.Status enumStatus = Lease.Status.valueOf(status);
		lease.setStatus(enumStatus);
		Lease updatedLease = leaseRepository.save(lease);
		return LeaseMapper.convertToLeaseOutputDto(updatedLease);

	}

}
