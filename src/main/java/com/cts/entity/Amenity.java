package com.cts.entity;

import java.time.LocalDate;

import jakarta.persistence.Entity;
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
@Table(name = "amenity")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Amenity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int amenityId;
	@ManyToOne
	@JoinColumn(name = "unit_id")
	private Unit unit;
	private String name;
	private String description;
	private LocalDate createdAt;
	
}
