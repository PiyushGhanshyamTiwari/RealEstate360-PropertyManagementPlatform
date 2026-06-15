package com.cts.entity;
 
import java.time.LocalDateTime;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import jakarta.persistence.*;
import lombok.*;
 
@Entity
@Table(name = "maintenance_log")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class MaintenanceLog {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int logId;
 
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id", nullable = false)
    private MaintenanceSchedule schedule;
 
    @Column(nullable = false)
    private String remarks;
 
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime logDate;
}