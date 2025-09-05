package com.backend.backend.api;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;
import java.util.Map;

@Schema(description = "Standard API error response")
public record ApiError(
        @Schema(example = "400") int status,
        @Schema(example = "Validation Failed") String error,
        @Schema(example = "2025-01-19T14:12:00Z") OffsetDateTime timestamp,
        @Schema(description = "Field-level errors") Map<String, String> fieldErrors,
        @Schema(example = "Invalid payload") String message,
        @Schema(example = "/api/v1/products") String path
) {}
