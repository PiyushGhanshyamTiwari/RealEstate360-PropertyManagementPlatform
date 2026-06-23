// MaintenanceScheduleRepository.java
package com.cts.repository;

import com.cts.entity.MaintenanceSchedule;
import com.cts.enums.MaintenanceStatus;
import com.cts.enums.Severity;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MaintenanceScheduleRepository extends JpaRepository<MaintenanceSchedule, Integer> {

	 List<MaintenanceSchedule> findByStatus(MaintenanceStatus status);

	    List<MaintenanceSchedule> findBySeverity(Severity severity);

	    List<MaintenanceSchedule> findByTenant_TenantId(int tenantId);

	    List<MaintenanceSchedule> findByTechnician_TechnicianId(int technicianId);

	    List<MaintenanceSchedule> findByTechnicianUserUserId(int userId);
}