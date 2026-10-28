package com.smartcampus.theatre.dto;

import com.smartcampus.theatre.model.BookedSeat;
import com.smartcampus.theatre.model.Booking;
import com.smartcampus.theatre.model.BookingStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

public record BookingResponse(
        Long id,
        String bookingCode,
        Long showId,
        String showTitle,
        LocalDateTime showTime,
        String hall,
        Long userId,
        String userName,
        List<String> seats,
        BigDecimal totalAmount,
        BookingStatus status,
        LocalDateTime createdAt,
        LocalDateTime cancelledAt
) {
    /** Must be called while the booking's lazy associations are still loadable (inside a transaction or after fetch join). */
    public static BookingResponse from(Booking b) {
        List<String> labels = b.getSeats().stream()
                .map(BookedSeat::getSeatLabel)
                .sorted(Comparator.comparing((String s) -> s.charAt(0)).thenComparingInt(s -> Integer.parseInt(s.substring(1))))
                .toList();
        return new BookingResponse(b.getId(), b.getBookingCode(), b.getShow().getId(), b.getShow().getTitle(),
                b.getShow().getShowTime(), b.getShow().getHall(), b.getUser().getId(), b.getUser().getName(),
                labels, b.getTotalAmount(), b.getStatus(), b.getCreatedAt(), b.getCancelledAt());
    }
}
