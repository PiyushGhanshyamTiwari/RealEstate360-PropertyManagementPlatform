package com.cts.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.cts.entity.AuditLog;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    @Query("SELECT a FROM AuditLog a WHERE a.userId = :userId")
    List<AuditLog> findByUserId(@Param("userId") Integer userId);

    @Query("SELECT a FROM AuditLog a WHERE a.action = :action")
    List<AuditLog> findByAction(@Param("action") String action);

    @Query("SELECT a FROM AuditLog a WHERE a.resourceType = :resourceType")
    List<AuditLog> findByResourceType(@Param("resourceType") String resourceType);
}
