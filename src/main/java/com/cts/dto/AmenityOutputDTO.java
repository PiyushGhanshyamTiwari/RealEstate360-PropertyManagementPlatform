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
public class AmenityOutputDTO {
	private int amenityId;
	private int unitId;
	private String name;
	private String description;
	private LocalDate createdAt;
	private String type;
    private Double areaSqFt;
    private Integer floor;
    private Double rentAmount;
    private Double depositAmount;
}
