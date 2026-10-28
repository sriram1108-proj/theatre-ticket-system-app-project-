package com.smartcampus.theatre.service;

import com.smartcampus.theatre.dto.BookingRequest;
import com.smartcampus.theatre.dto.BookingResponse;
import com.smartcampus.theatre.exception.BadRequestException;
import com.smartcampus.theatre.exception.ConflictException;
import com.smartcampus.theatre.exception.ResourceNotFoundException;
import com.smartcampus.theatre.model.AppUser;
import com.smartcampus.theatre.model.BookedSeat;
import com.smartcampus.theatre.model.Booking;
import com.smartcampus.theatre.model.BookingStatus;
import com.smartcampus.theatre.model.Show;
import com.smartcampus.theatre.repository.BookedSeatRepository;
import com.smartcampus.theatre.repository.BookingRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookingService {

    private static final Pattern SEAT_PATTERN = Pattern.compile("^[A-Z][0-9]{1,2}$");

    private final BookingRepository bookingRepository;
    private final BookedSeatRepository bookedSeatRepository;
    private final ShowService showService;
    private final UserService userService;
    private final int maxSeatsPerBooking;
    private final int cancelCutoffHours;

    public BookingService(BookingRepository bookingRepository,
                          BookedSeatRepository bookedSeatRepository,
                          ShowService showService,
                          UserService userService,
                          @Value("${theatre.booking.max-seats-per-booking:6}") int maxSeatsPerBooking,
                          @Value("${theatre.booking.cancel-cutoff-hours:2}") int cancelCutoffHours) {
        this.bookingRepository = bookingRepository;
        this.bookedSeatRepository = bookedSeatRepository;
        this.showService = showService;
        this.userService = userService;
        this.maxSeatsPerBooking = maxSeatsPerBooking;
        this.cancelCutoffHours = cancelCutoffHours;
    }

    @Transactional
    public BookingResponse book(BookingRequest req) {
        Show show = showService.find(req.showId());
        AppUser user = userService.find(req.userId());

        if (!show.getShowTime().isAfter(LocalDateTime.now())) {
            throw new BadRequestException("Bookings are closed: this show has already started or finished");
        }

        Set<String> seats = normaliseAndValidate(req.seats(), show);

        List<String> taken = bookedSeatRepository.findTakenAmong(show.getId(), seats);
        if (!taken.isEmpty()) {
            throw new ConflictException("Seats already booked: " + String.join(", ", taken));
        }

        Booking booking = new Booking();
        booking.setBookingCode("TKT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        booking.setUser(user);
        booking.setShow(show);
        booking.setTotalAmount(show.getTicketPrice().multiply(BigDecimal.valueOf(seats.size())));
        for (String label : seats) {
            booking.getSeats().add(new BookedSeat(show, booking, label));
        }

        try {
            // flush now so the unique constraint is checked inside this method
            bookingRepository.saveAndFlush(booking);
        } catch (DataIntegrityViolationException ex) {
            // another user grabbed one of the seats between our check and the insert
            throw new ConflictException("One or more seats were just booked by someone else. Please pick different seats.");
        }
        return BookingResponse.from(booking);
    }

    @Transactional(readOnly = true)
    public BookingResponse get(Long id) {
        return BookingResponse.from(bookingRepository.findDetailedById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id " + id)));
    }

    @Transactional(readOnly = true)
    public BookingResponse getByCode(String code) {
        return BookingResponse.from(bookingRepository.findByBookingCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with code " + code)));
    }

    @Transactional(readOnly = true)
    public List<BookingResponse> listForUser(Long userId) {
        userService.find(userId); // 404 if user does not exist
        return bookingRepository.findByUserId(userId).stream().map(BookingResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<BookingResponse> listForShow(Long showId) {
        showService.find(showId);
        return bookingRepository.findByShowId(showId).stream().map(BookingResponse::from).toList();
    }

    @Transactional
    public BookingResponse cancel(Long id) {
        Booking booking = bookingRepository.findDetailedById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id " + id));

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new ConflictException("Booking is already cancelled");
        }
        LocalDateTime cutoff = booking.getShow().getShowTime().minusHours(cancelCutoffHours);
        if (LocalDateTime.now().isAfter(cutoff)) {
            throw new BadRequestException("Cancellations are not allowed within " + cancelCutoffHours + " hours of the show");
        }

        // Snapshot the seat labels for the response, then free the seats.
        BookingResponse snapshot = BookingResponse.from(booking);
        booking.getSeats().clear(); // orphanRemoval deletes the BookedSeat rows -> seats become available
        booking.setStatus(BookingStatus.CANCELLED);
        booking.setCancelledAt(LocalDateTime.now());
        bookingRepository.save(booking);

        return new BookingResponse(snapshot.id(), snapshot.bookingCode(), snapshot.showId(), snapshot.showTitle(),
                snapshot.showTime(), snapshot.hall(), snapshot.userId(), snapshot.userName(), snapshot.seats(),
                snapshot.totalAmount(), BookingStatus.CANCELLED, snapshot.createdAt(), booking.getCancelledAt());
    }

    private Set<String> normaliseAndValidate(List<String> raw, Show show) {
        Set<String> seats = new LinkedHashSet<>();
        for (String s : raw) {
            if (s == null) throw new BadRequestException("Seat label cannot be null");
            String label = s.trim().toUpperCase();
            if (!SEAT_PATTERN.matcher(label).matches()) {
                throw new BadRequestException("Invalid seat label '" + s + "' (expected format like A1, B12)");
            }
            int row = label.charAt(0) - 'A';
            int number = Integer.parseInt(label.substring(1));
            if (row >= show.getTotalRows() || number < 1 || number > show.getSeatsPerRow()) {
                throw new BadRequestException("Seat " + label + " does not exist in " + show.getHall());
            }
            if (!seats.add(label)) {
                throw new BadRequestException("Seat " + label + " was listed more than once");
            }
        }
        if (seats.size() > maxSeatsPerBooking) {
            throw new BadRequestException("You can book at most " + maxSeatsPerBooking + " seats per booking");
        }
        return seats;
    }
}
