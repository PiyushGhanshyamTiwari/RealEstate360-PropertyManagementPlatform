package com.cts.mapper;

import com.cts.dto.AccountOfficerInputDto;
import com.cts.dto.AccountOfficerOutputDto;
import com.cts.entity.AccountOfficer;

public class AccountOfficerMapper {

	public static AccountOfficer convertToAccountOfficer(AccountOfficerInputDto input) {
		return AccountOfficer.builder()
				.fullName(input.getFullName())
				.emailId(input.getEmailId())
				.phone(input.getPhone())
				.build();
	}

	public static AccountOfficerOutputDto convertToAccountOfficerOutputDto(AccountOfficer officer) {
		return AccountOfficerOutputDto.builder()
				.officerId(officer.getOfficerId())
				.fullName(officer.getFullName())
				.emailId(officer.getEmailId())
				.phone(officer.getPhone())
				.createdAt(officer.getCreatedAt())
				.build();
	}
}