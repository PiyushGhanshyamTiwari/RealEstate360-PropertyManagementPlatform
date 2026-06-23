package com.cts.dto;

import jakarta.validation.constraints.NotBlank;

import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;

import lombok.Builder;

import lombok.Data;

import lombok.NoArgsConstructor;

@Data

@NoArgsConstructor

@AllArgsConstructor

@Builder

public class ManagerAssignRequestDTO {

    @NotNull(message = "Technician ID is required")
    private int userId;
    
    @NotBlank(message = "Severity is required (LOW, MEDIUM, HIGH)")
    private String severity;

}

