package com.cts.mapper;

import com.cts.dto.TenantProfileInputDTO;
import com.cts.dto.TenantProfileOutputDTO;

import com.cts.entity.TenantProfile;
import com.cts.entity.User;

public class TenantProfileMapper {

    
    public static TenantProfile convertToTenantProfile(TenantProfileInputDTO input, User user, String fileName) {

        return TenantProfile.builder()
                .user(user)
                .Address(input.getAddress())
                .documentType(TenantProfile.DocumentType.valueOf(input.getDocumentType().toUpperCase()))
                .documentFileRef(fileName)   
                .build();
    }

    
    public static TenantProfileOutputDTO convertToTenantProfileOutputDto(TenantProfile tenantProfile) {

        return TenantProfileOutputDTO.builder()
                .tenantId(tenantProfile.getTenantId())
                .address(tenantProfile.getAddress())
                .createdAt(tenantProfile.getCreatedAt())
                .documentType(tenantProfile.getDocumentType().name())
                .documentFileRef(tenantProfile.getDocumentFileRef()) 
                .userName(tenantProfile.getUser().getUserName())
                .phone(tenantProfile.getUser().getPhone())
                .emailId(tenantProfile.getUser().getEmailId())
                .build();
    }
}