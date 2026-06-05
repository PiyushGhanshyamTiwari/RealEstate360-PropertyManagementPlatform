package com.cts.dto;
 
import lombok.*;
import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.EntityListeners;
 
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class TenantProfileOutputDTO {
    private int tenantId;
    private String address;
    @CreatedDate
    private LocalDateTime createdAt;
 
    private String documentType;     
    private String documentFileRef;
    private String userName;
    private long phone;
    private String emailId;
}