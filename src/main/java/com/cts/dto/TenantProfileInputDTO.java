package com.cts.dto;
 
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
 
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TenantProfileInputDTO {
    @NotNull(message = "User Id is required")
    private Integer userId;
 
    @NotBlank(message = "Address cannot be blank")
    private String address;
 
    @NotBlank(message = "Document type is required")
    private String documentType;   
    @Schema(type = "string", format = "binary")
    private MultipartFile documentFileRef;

	

	
}