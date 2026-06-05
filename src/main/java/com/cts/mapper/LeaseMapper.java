package com.cts.mapper;



import com.cts.dto.LeaseInputDTO;
import com.cts.dto.LeaseOutputDTO;
import com.cts.entity.Lease;
import com.cts.entity.TenantProfile;
import com.cts.entity.Unit;

public class LeaseMapper {
	public static Lease convertToLease(LeaseInputDTO input,Unit unit, TenantProfile tenant) {
		return Lease.builder()
				.unit(unit)
				.tenantProfile(tenant)
				.startDate(input.getStartDate())
				.endDate(input.getEndDate())
				.status(Lease.Status.Review)
				.build();
	}
	public static LeaseOutputDTO convertToLeaseOutputDto(Lease lease) {
		return LeaseOutputDTO.builder()
				.leaseId(lease.getLeaseId())
				.unitId(lease.getUnit().getUnitId())
				.ownerId(lease.getUnit().getProperty().getUser().getUserId())
				.ownerName(lease.getUnit().getProperty().getUser().getUserName())
				.tenantId(lease.getTenantProfile().getTenantId())
				.tenantName(lease.getTenantProfile().getUser().getUserName())
				.startDate(lease.getStartDate())
				.endDate(lease.getEndDate())
				.rentAmount(lease.getUnit().getRentAmount())
				.depositAmount(lease.getUnit().getDepositAmount())
				.status(lease.getStatus().name())
				.createdAt(lease.getCreatedDate())
				.build();
				}
}
