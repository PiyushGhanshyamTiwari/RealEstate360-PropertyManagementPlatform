package com.cts.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PropertyOutputDTO {
	
	private int propertyId;
	private String propertyName;
	private String propertyAddress;
	private String propertyCity;
	private String propertyState;
	private String propertyPostalCode;
	private String propertyCountry;
	private String status;
	private LocalDate createdAt;
	private LocalDate updatedAt;
	private int ownerId;  

}
