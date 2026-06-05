package com.cts.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApplicationInputDTO {
	@NotNull(message = "UnitId is required")
	private Integer unitId;
	@NotNull(message = "User Id is required")
    private Integer userId;
    
}
