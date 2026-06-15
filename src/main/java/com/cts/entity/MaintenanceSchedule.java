package com.cts.entity;
 
import com.cts.enums.MaintenanceStatus;
import com.cts.enums.Severity;
 
import java.time.LocalDate;
import java.time.LocalDateTime;
 
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
 
import jakarta.persistence.*;
 
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
 
@Entity
@Table(name = "maintenance_schedule")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class MaintenanceSchedule {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer scheduleId;
    
    @ManyToOne
    @JoinColumn(name="tenant_id")
    private TenantProfile tenant;
 
    @ManyToOne
    @JoinColumn(name="unit_id")
    private Unit unit;
 
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "technician_id")
    private Technician technician;
 
    @Column
    private String issueDescription;
 
    @Enumerated(EnumType.STRING)
    @Column
    private Severity severity;
 
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MaintenanceStatus status;
 
    private LocalDate scheduledDate;
 
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
 
    @LastModifiedDate
    private LocalDateTime updatedAt;
}