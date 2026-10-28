package com.smartcampus.theatre.controller;

import com.smartcampus.theatre.dto.BookingRequest;
import com.smartcampus.theatre.dto.BookingResponse;
import com.smartcampus.theatre.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingResponse book(@Valid @RequestBody BookingRequest req) {
        return bookingService.book(req);
    }

    @GetMapping("/{id}")
    public BookingResponse get(@PathVariable Long id) {
        return bookingService.get(id);
    }

    @GetMapping("/code/{code}")
    public BookingResponse getByCode(@PathVariable String code) {
        return bookingService.getByCode(code);
    }

    /** Cancels the booking and releases its seats. */
    @DeleteMapping("/{id}")
    public BookingResponse cancel(@PathVariable Long id) {
        return bookingService.cancel(id);
    }
}
