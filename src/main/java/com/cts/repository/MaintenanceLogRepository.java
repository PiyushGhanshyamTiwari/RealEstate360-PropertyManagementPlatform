package com.cts.repository;
 
import com.cts.entity.MaintenanceLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
 
@Repository
public interface MaintenanceLogRepository extends JpaRepository<MaintenanceLog, Integer> {
 
    Page<MaintenanceLog> findBySchedule_ScheduleId(int i, Pageable pageable);
}