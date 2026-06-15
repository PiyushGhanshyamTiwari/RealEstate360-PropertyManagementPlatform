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
public class TenantIssueRequestDTO {
 
    @NotNull(message = "Tenant ID is required")
    private Integer tenantId;
 
    @NotNull(message = "Unit ID is required")
    private Integer unitId;
 
    @NotBlank(message = "Issue description is required")
    private String issueDescription;
}
 