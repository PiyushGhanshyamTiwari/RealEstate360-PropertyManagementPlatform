package com.cts.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
	@ExceptionHandler(PropertyIdNotFoundException.class)
	public ResponseEntity<?> handlePropertyIdNotFoundException(PropertyIdNotFoundException ex){
		return new ResponseEntity<>(ex.getMessage(),HttpStatus.NOT_FOUND);
	}
	@ExceptionHandler(OwnerIdNotFoundException.class)
	public ResponseEntity<?> handleOwnerIdNotFoundException(OwnerIdNotFoundException ex){
		return new ResponseEntity<>(ex.getMessage(),HttpStatus.NOT_FOUND);
	}
	@ExceptionHandler(TenantIdNotFoundException.class)
	public ResponseEntity<?> handleTenantIdNotFoundException(TenantIdNotFoundException ex){
		return new ResponseEntity<>(ex.getMessage(),HttpStatus.NOT_FOUND);
	}
	@ExceptionHandler(UserIdNotFoundException.class)
	public ResponseEntity<?> handleUserIdNotFoundException(UserIdNotFoundException ex){
		return new ResponseEntity<>(ex.getMessage(),HttpStatus.NOT_FOUND);
	}
	@ExceptionHandler(UnitIdNotFoundException.class)
	public ResponseEntity<?> handleUnitIdNotFoundException(UnitIdNotFoundException ex){
		return new ResponseEntity<>(ex.getMessage(),HttpStatus.NOT_FOUND);
	}
}
