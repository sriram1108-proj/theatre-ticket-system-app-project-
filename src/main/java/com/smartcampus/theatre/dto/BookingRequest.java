package com.smartcampus.theatre.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record BookingRequest(
        @NotNull Long userId,
        @NotNull Long showId,
        @NotEmpty List<String> seats
) {}
