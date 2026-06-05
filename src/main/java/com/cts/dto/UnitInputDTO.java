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
public class UnitInputDTO {

	
    private String type;
    private double areaSqFt;
    private int floor;
    private double rentAmount;
    private double depositAmount;
    private LocalDate availableFrom;
    private int propertyId;
   

}
