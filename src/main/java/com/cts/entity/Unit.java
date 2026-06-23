package com.cts.entity;

import java.time.LocalDate;

import com.cts.enums.UnitStatus;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Unit {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int unitId;
	
	//Many units belong to one property
	@ManyToOne
	@JoinColumn(name="property_id", nullable=false)
	private Property property;
	
	private String type;
	private double areaSqFt;
	private int floor;
	private double rentAmount;
	private double depositAmount;
	
	@Enumerated(EnumType.STRING)
    @Builder.Default
    private UnitStatus status = UnitStatus.AVAILABLE;

	private LocalDate AvailableFrom;
	private LocalDate createdAt;
	private LocalDate updatedAt;

}
