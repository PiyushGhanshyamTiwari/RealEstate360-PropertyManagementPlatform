package com.cts.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;



import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Lease")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Lease {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int leaseId;
	@ManyToOne
	@JoinColumn(name = "unitId")
	private Unit unit;
	@ManyToOne
	@JoinColumn(name = "tenantId")
	private TenantProfile tenantProfile;
	private LocalDate startDate;
	private LocalDate endDate;
	@CreatedDate
	private LocalDateTime createdDate;
	@Enumerated(EnumType.STRING)
	private Status status;
	public enum Status{
		Review,
		Agreed,
		Cancelled
	}
}
