package com.cts.dto;

import java.time.LocalDate;

import com.cts.enums.UnitStatus;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UnitInputDTO {
    @NotBlank(message = "Unit type cannot be blank")
    private String type;

    @Positive(message = "Area must be positive")
    private double areaSqFt;

    @NotNull(message="floor cannnot be blank")
    @Min(value = 0, message = "Floor number cannot be negative")
    private Integer floor;

    @Positive(message = "Rent amount must be positive")
    @NotNull(message="Rent amount is required")
    private double rentAmount;

    @Positive(message = "Deposit amount must be positive")
    @NotNull(message="deposit amount is required")
    private double depositAmount;

    private UnitStatus status;


    @NotNull(message = "Available from date is required")
    private LocalDate availableFrom;

    private int propertyId;
}

