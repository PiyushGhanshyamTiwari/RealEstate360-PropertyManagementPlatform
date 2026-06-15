
package com.cts.exception;

public class MaintenanceScheduleNotFoundException extends RuntimeException {
    public MaintenanceScheduleNotFoundException(int i) {
        super("Maintenance schedule not found with ID: " + i);
    }
}