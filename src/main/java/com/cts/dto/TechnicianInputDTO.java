package com.cts.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

import jakarta.validation.constraints.*;
import lombok.*;

 
@NoArgsConstructor
@AllArgsConstructor
@Data
public class TechnicianInputDTO {

    @NotNull(message = "User ID is required")
    private int userId;

    @NotNull(message = "Specialization is required")
    private String specialization;

    @NotBlank(message = "City cannot be empty")
    private String city;
    
    @NotNull
    private LocalDate hireDate;
}
