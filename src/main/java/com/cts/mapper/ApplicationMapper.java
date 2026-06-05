package com.cts.mapper;

import com.cts.dto.ApplicationInputDTO;
import com.cts.dto.ApplicationOutputDTO;
import com.cts.entity.Application;
import com.cts.entity.Unit;
import com.cts.entity.User;

public class ApplicationMapper {
	public static Application convertToApllication(ApplicationInputDTO input,Unit unit,User user) {
		return Application.builder()
				.unit(unit)
				.user(user)
				.status(Application.Status.Pending)
				.build();
	}
	
	public static ApplicationOutputDTO convertToApplicationOutputDto(Application application) {
		return ApplicationOutputDTO.builder()
				.applicationId(application.getApplicationId())
				.unitId(application.getUnit().getUnitId())
				.userId(application.getUser().getUserId())
				.submittedAt(application.getSubmittedAt())
				.status(application.getStatus().name())
				.type(application.getUnit().getType())
				.propertyName(application.getUnit().getProperty().getPropertyName())
				.address(application.getUnit().getProperty().getPropertyAddress())
				.city(application.getUnit().getProperty().getPropertyCity())
				.build();
				
	}
}
