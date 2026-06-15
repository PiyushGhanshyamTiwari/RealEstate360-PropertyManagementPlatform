package com.cts.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegistrationOutputDTO {

    private int userId;
    private String userName;
    private String emailId;
    private long phone;
    private String role;
    private LocalDate registeredOn;
	

}
