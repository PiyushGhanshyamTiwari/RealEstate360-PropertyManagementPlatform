package com.cts.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "property_photo")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@EntityListeners(AuditingEntityListener.class)
public class PropertyPhoto {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int photoId;
	@ManyToOne
	@JoinColumn(name = "unit_id")
	private Unit unit;
	private String fileRef;
	private String caption;
	private String uploadedBy;
	@CreatedDate
	private LocalDateTime uploadedAt;
}