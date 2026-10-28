package com.smartcampus.theatre.controller;

import com.smartcampus.theatre.dto.BookingResponse;
import com.smartcampus.theatre.dto.UserRequest;
import com.smartcampus.theatre.dto.UserResponse;
import com.smartcampus.theatre.service.BookingService;
import com.smartcampus.theatre.service.UserService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final BookingService bookingService;

    public UserController(UserService userService, BookingService bookingService) {
        this.userService = userService;
        this.bookingService = bookingService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse register(@Valid @RequestBody UserRequest req) {
        return userService.register(req);
    }

    @GetMapping
    public List<UserResponse> list() {
        return userService.list();
    }

    @GetMapping("/{id}")
    public UserResponse get(@PathVariable Long id) {
        return userService.get(id);
    }

    @GetMapping("/{id}/bookings")
    public List<BookingResponse> bookings(@PathVariable Long id) {
        return bookingService.listForUser(id);
    }
}
