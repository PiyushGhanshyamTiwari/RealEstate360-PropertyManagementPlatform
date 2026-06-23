package com.cts.exception;




import org.springframework.boot.autoconfigure.info.ProjectInfoProperties.Build;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.cts.dto.ErrorResponseDTO;

@ControllerAdvice
public class GlobalExceptionHandler {
	
	@ExceptionHandler(SpecializationMismatchException.class)

    public ResponseEntity<?> handleSpecializationMismatch(SpecializationMismatchException ex) {

        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);

    }
	
	@ExceptionHandler(PropertyIdNotFoundException.class)
	public ResponseEntity<?> handlePropertyIdNotFoundException(PropertyIdNotFoundException ex) {
		return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(OwnerIdNotFoundException.class)
	public ResponseEntity<?> handleOwnerIdNotFoundException(OwnerIdNotFoundException ex) {
		return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(TenantIdNotFoundException.class)
	public ResponseEntity<?> handleTenantIdNotFoundException(TenantIdNotFoundException ex) {
		return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(UserIdNotFoundException.class)
	public ResponseEntity<?> handleUserIdNotFoundException(UserIdNotFoundException ex) {
		return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(UnitIdNotFoundException.class)
	public ResponseEntity<?> handleUnitIdNotFoundException(UnitIdNotFoundException ex) {
		return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(MaintenanceScheduleNotFoundException.class)
	public ResponseEntity<?> handleScheduleNotFound(MaintenanceScheduleNotFoundException ex) {
		return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(TechnicianNotFoundException.class)
	public ResponseEntity<?> handleTechnicianNotFound(TechnicianNotFoundException ex) {
		return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(InvalidStatusTransitionException.class)
	public ResponseEntity<?> handleInvalidStatusTransition(InvalidStatusTransitionException ex) {
		return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(MaintenanceLogNotFoundException.class)
	public ResponseEntity<?> handleLogNotFound(MaintenanceLogNotFoundException ex) {
		return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(TechnicianNotAvailableException.class)

    public ResponseEntity<?> handleTechnicianNotAvailable(TechnicianNotAvailableException ex) {

        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);

    }
	
	@ExceptionHandler(TechnicianInactiveException.class)

    public ResponseEntity<?> handleTechnicianInactive(TechnicianInactiveException ex) {

        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);

    }
	
	@ExceptionHandler(UnauthorizedTechnicianException.class)

    public ResponseEntity<?> handleUnauthorizedTechnician(UnauthorizedTechnicianException ex) {

        return new ResponseEntity<>(ex.getMessage(), HttpStatus.FORBIDDEN);

    }
	
	@ExceptionHandler(NoTechnicianAssignedException.class)

    public ResponseEntity<?> handleNoTechnicianAssigned(NoTechnicianAssignedException ex) {

        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);

    }

	//helper method 
	private ResponseEntity<ErrorResponseDTO> build(HttpStatus status, String error, String message) {
		ErrorResponseDTO response = ErrorResponseDTO.builder().status(status.value()).error(error).message(message).build();
		return new ResponseEntity<>(response, status);
	}

	@ExceptionHandler(AuthenticationException.class)
	public ResponseEntity<ErrorResponseDTO> handleAuthenticationException(AuthenticationException ex) {
		return build(HttpStatus.UNAUTHORIZED, "Unauthorized",
				"Authentication required. Please login and provide a valid JWT token.");
	}

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<?> handleAccessDeniedException(AccessDeniedException ex) {
		return new ResponseEntity<>("You do not have permission to perform this action",HttpStatus.FORBIDDEN);
	}
}
