package com.cts.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "invoice")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Invoice {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int invoiceId;

	@ManyToOne
	@JoinColumn(name = "tenantId")
	private TenantProfile tenantProfile;

	@ManyToOne
	@JoinColumn(name = "leaseId")
	private Lease lease;

	private LocalDate periodStart;
	
	private LocalDate periodEnd;
	
	private LocalDate dueDate;

	@Enumerated(EnumType.STRING)
	private Status status;
	
	@CreatedDate
	private LocalDateTime generatedAt;

	public enum Status {
		PENDING, PAID, OVERDUE
	}
}