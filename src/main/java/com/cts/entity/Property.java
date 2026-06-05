package com.cts.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
public class Property {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int propertyId;
	
	@Column(name="name", nullable=false)
	private String propertyName;
	
	@Column(name="address", nullable=false)
	private String propertyAddress;
	private String propertyCity;
	private String propertyState;
	private String propertyPostalCode;
	private String propertyCountry;
	private String status;
	private LocalDate createdAt;
	private LocalDate updatedAt;
	
	
	@ManyToOne
	@JoinColumn(name = "owner_id")
	private User user;
	

}
