package com.smartcampus.theatre.controller;

import com.smartcampus.theatre.dto.BookingResponse;
import com.smartcampus.theatre.dto.SeatMapResponse;
import com.smartcampus.theatre.dto.ShowRequest;
import com.smartcampus.theatre.dto.ShowResponse;
import com.smartcampus.theatre.service.BookingService;
import com.smartcampus.theatre.service.ShowService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shows")
public class ShowController {

    private final ShowService showService;
    private final BookingService bookingService;

    public ShowController(ShowService showService, BookingService bookingService) {
        this.showService = showService;
        this.bookingService = bookingService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ShowResponse create(@Valid @RequestBody ShowRequest req) {
        return showService.create(req);
    }

    /** GET /api/shows?upcoming=true&search=hamlet */
    @GetMapping
    public List<ShowResponse> list(@RequestParam(defaultValue = "false") boolean upcoming,
                                   @RequestParam(required = false) String search) {
        return showService.list(upcoming, search);
    }

    @GetMapping("/{id}")
    public ShowResponse get(@PathVariable Long id) {
        return showService.get(id);
    }

    @PutMapping("/{id}")
    public ShowResponse update(@PathVariable Long id, @Valid @RequestBody ShowRequest req) {
        return showService.update(id, req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        showService.delete(id);
    }

    @GetMapping("/{id}/seats")
    public SeatMapResponse seats(@PathVariable Long id) {
        return showService.seatMap(id);
    }

    @GetMapping("/{id}/bookings")
    public List<BookingResponse> bookings(@PathVariable Long id) {
        return bookingService.listForShow(id);
    }
}
