package com.smartcampus.theatre.dto;

import com.smartcampus.theatre.model.Show;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ShowResponse(
        Long id,
        String title,
        String description,
        String genre,
        int durationMinutes,
        LocalDateTime showTime,
        String hall,
        BigDecimal ticketPrice,
        int totalRows,
        int seatsPerRow,
        int capacity,
        long availableSeats
) {
    public static ShowResponse from(Show s, long bookedCount) {
        return new ShowResponse(s.getId(), s.getTitle(), s.getDescription(), s.getGenre(),
                s.getDurationMinutes(), s.getShowTime(), s.getHall(), s.getTicketPrice(),
                s.getTotalRows(), s.getSeatsPerRow(), s.getCapacity(), s.getCapacity() - bookedCount);
    }
}
