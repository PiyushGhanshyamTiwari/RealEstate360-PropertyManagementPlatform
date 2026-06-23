package com.cts.repository;

import com.cts.entity.Lease;
import com.cts.entity.TenantProfile;
import com.cts.entity.Unit;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LeaseRepository extends JpaRepository<Lease, Integer> {

	boolean existsByUnit_UnitIdAndTenantProfile_TenantId(int unitId, int userId);

}
