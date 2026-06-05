package com.cts.entity;



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
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tenant_profile")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class TenantProfile {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int tenantId;
	
	@OneToOne
	@JoinColumn(name = "user_id")
	private User user;
	
	private String documentFileRef;
	private String Address;
	
	@CreatedDate
	private LocalDateTime createdAt;

	@Enumerated(EnumType.STRING)
	private DocumentType documentType;
	public enum DocumentType {
        AADHAAR,
        PAN,
        VoterId
    }

	

}
