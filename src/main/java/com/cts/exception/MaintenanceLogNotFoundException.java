// MaintenanceLogNotFoundException.java
package com.cts.exception;

public class MaintenanceLogNotFoundException extends RuntimeException {
    public MaintenanceLogNotFoundException(Long id) {
        super("Maintenance log not found with ID: " + id);
    }
}