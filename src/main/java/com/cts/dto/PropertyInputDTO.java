package com.cts.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PropertyInputDTO {


	@NotBlank(message="Property name can not be null")
	private String propertyName;
	@NotBlank(message = "Property Address can not be null")
	private String propertyAddress;
	@NotBlank(message= "Property city can not be null")
	private String propertyCity;
	@NotBlank(message= "Property state can not be null")
	private String propertyState;
	@Pattern(regexp = "\\d{6}", message = "Postal code must be exactly 6 digits")
	private String propertyPostalCode;
	@NotBlank(message = "Property Country should not be null")
	private String propertyCountry;
	
	
}
