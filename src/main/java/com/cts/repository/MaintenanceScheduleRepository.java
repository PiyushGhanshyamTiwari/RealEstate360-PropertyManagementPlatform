// MaintenanceScheduleRepository.java
package com.cts.repository;
 
import com.cts.entity.MaintenanceSchedule;
import com.cts.enums.MaintenanceStatus;
import com.cts.enums.Severity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
 
@Repository
public interface MaintenanceScheduleRepository extends JpaRepository<MaintenanceSchedule, Integer> {
 
    Page<MaintenanceSchedule> findByStatus(MaintenanceStatus status, Pageable pageable);
 
    Page<MaintenanceSchedule> findBySeverity(Severity severity, Pageable pageable);
 
    Page<MaintenanceSchedule> findByTenant_TenantId(int tenantId, Pageable pageable);
 
    Page<MaintenanceSchedule> findByTechnician_TechnicianId(Long technicianId, Pageable pageable);
}