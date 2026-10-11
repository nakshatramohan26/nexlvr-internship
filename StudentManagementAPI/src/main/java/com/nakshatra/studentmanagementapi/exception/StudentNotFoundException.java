package com.nakshatra.studentmanagementapi.exception;

/**
 * Thrown by the service layer when no student exists with the requested id.
 * GlobalExceptionHandler turns it into an HTTP 404 response.
 */
public class StudentNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public StudentNotFoundException(Long id) {
        super("Student with id " + id + " was not found");
    }
}
