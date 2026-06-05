package com.cts.dto;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.EntityListeners;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class ApplicationOutputDTO {
	private int applicationId;
	private int unitId;
	private int userId;
	@CreatedDate
	private LocalDateTime submittedAt;
	private String status;
	private String type;
	private String propertyName;
	private String address;
	private String city;
}
