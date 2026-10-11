package com.nakshatra.studentmanagementapi.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.Map;

/**
 * The single JSON shape used for every error returned by this API.
 * fieldErrors is only present for validation failures (400).
 */
@Schema(description = "Error response returned for every failed request")
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
        @Schema(description = "When the error happened (UTC)", example = "2026-10-11T09:45:00Z")
        Instant timestamp,
        @Schema(description = "HTTP status code", example = "404")
        int status,
        @Schema(description = "HTTP status text", example = "Not Found")
        String error,
        @Schema(description = "Short explanation of what went wrong", example = "Student with id 99 was not found")
        String message,
        @Schema(description = "Validation problems: field name -> message (validation errors only)")
        Map<String, String> fieldErrors) {
}
