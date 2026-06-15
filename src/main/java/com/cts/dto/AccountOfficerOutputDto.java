package com.cts.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountOfficerOutputDto {
	private int officerId;
	private String fullName;
	private String emailId;
	private long phone;
	private LocalDateTime createdAt;
}