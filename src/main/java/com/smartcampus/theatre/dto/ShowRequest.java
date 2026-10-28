package com.smartcampus.theatre.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ShowRequest(
        @NotBlank String title,
        @Size(max = 1000) String description,
        String genre,
        @Min(1) int durationMinutes,
        @NotNull LocalDateTime showTime,
        @NotBlank String hall,
        @NotNull @DecimalMin("0.0") BigDecimal ticketPrice,
        @Min(1) @Max(26) int totalRows,
        @Min(1) @Max(50) int seatsPerRow
) {}
