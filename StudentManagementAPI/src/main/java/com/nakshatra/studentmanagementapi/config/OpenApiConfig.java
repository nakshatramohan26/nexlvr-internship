package com.nakshatra.studentmanagementapi.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Title and description shown at the top of Swagger UI and in /v3/api-docs. */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI studentApiInfo() {
        return new OpenAPI().info(new Info()
                .title("Student Management API")
                .version("0.0.1")
                .description("Simple REST API to create, read, update and delete student records. "
                        + "Data is stored in memory only and is lost when the application restarts. "
                        + "Built by Nakshatra M for the NexLevr Java Development internship."));
    }
}
