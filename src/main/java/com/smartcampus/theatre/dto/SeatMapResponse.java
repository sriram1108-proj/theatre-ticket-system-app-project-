package com.smartcampus.theatre.dto;

import java.util.List;

public record SeatMapResponse(
        Long showId,
        String title,
        int totalRows,
        int seatsPerRow,
        int capacity,
        int availableCount,
        List<String> bookedSeats,
        List<String> availableSeats
) {}
