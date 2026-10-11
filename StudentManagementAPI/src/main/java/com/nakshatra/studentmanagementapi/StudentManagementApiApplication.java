package com.nakshatra.studentmanagementapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point. @SpringBootApplication turns on component scanning for this package
 * and all sub-packages (controller, service, exception, config), auto-configuration
 * and an embedded Tomcat server.
 */
@SpringBootApplication
public class StudentManagementApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(StudentManagementApiApplication.class, args);
    }
}
