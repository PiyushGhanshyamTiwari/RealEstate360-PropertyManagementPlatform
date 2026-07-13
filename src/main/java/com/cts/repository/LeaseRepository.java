package com.cts.repository;

import com.cts.entity.Lease;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LeaseRepository extends JpaRepository<Lease, Integer> {

	boolean existsByUnit_UnitIdAndTenantProfile_TenantId(int unitId, int userId);

	/** Leases belonging to all units under properties owned by the given userId */
	List<Lease> findByUnit_Property_User_UserId(int ownerUserId);

	/** Leases belonging to a specific tenant profile */
	List<Lease> findByTenantProfile_TenantId(int tenantId);

}
