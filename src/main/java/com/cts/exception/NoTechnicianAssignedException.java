package com.cts.exception;
 
public class NoTechnicianAssignedException extends RuntimeException {
    public NoTechnicianAssignedException(String message) {
        super(message);
    }
}