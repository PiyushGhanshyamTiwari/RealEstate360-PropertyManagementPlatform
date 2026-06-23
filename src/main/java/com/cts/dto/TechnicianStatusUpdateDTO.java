package com.cts.dto;
 
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
 
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TechnicianStatusUpdateDTO {
 
    @NotBlank(message = "Status is required (IN_PROGRESS, RESOLVED, CLOSED)")
    private String status;
}