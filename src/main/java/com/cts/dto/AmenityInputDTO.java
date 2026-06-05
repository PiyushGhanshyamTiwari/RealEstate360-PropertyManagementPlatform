package com.cts.dto;

import java.time.LocalDate;

import com.cts.entity.Unit;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class AmenityInputDTO {
	
	private String name;
	private String description;
	private LocalDate createdAt;
}
