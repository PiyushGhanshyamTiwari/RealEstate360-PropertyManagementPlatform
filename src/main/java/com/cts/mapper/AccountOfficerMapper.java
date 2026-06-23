package com.cts.mapper;

import com.cts.dto.AccountOfficerInputDto;

import com.cts.dto.AccountOfficerOutputDto;

import com.cts.entity.AccountOfficer;

import com.cts.entity.User;

public class AccountOfficerMapper {

    public static AccountOfficer convertToAccountOfficer(AccountOfficerInputDto input, User user) {

        return AccountOfficer.builder()
                .user(user)
                .fullName(input.getFullName())
                .address(input.getAddress())
                .build();

    }

    public static AccountOfficerOutputDto convertToAccountOfficerOutputDto(AccountOfficer officer) {

        return AccountOfficerOutputDto.builder()
                .officerId(officer.getOfficerId())
                .fullName(officer.getFullName())
                .emailId(officer.getUser().getEmailId())
                .phone(officer.getUser().getPhone())
                .address(officer.getAddress())
                .createdAt(officer.getCreatedAt())
                .build();

    }

}

