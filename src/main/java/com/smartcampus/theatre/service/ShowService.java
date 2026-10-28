package com.smartcampus.theatre.service;

import com.smartcampus.theatre.dto.SeatMapResponse;
import com.smartcampus.theatre.dto.ShowRequest;
import com.smartcampus.theatre.dto.ShowResponse;
import com.smartcampus.theatre.exception.BadRequestException;
import com.smartcampus.theatre.exception.ConflictException;
import com.smartcampus.theatre.exception.ResourceNotFoundException;
import com.smartcampus.theatre.model.Show;
import com.smartcampus.theatre.repository.BookedSeatRepository;
import com.smartcampus.theatre.repository.BookingRepository;
import com.smartcampus.theatre.repository.ShowRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ShowService {

    private final ShowRepository showRepository;
    private final BookedSeatRepository bookedSeatRepository;
    private final BookingRepository bookingRepository;

    public ShowService(ShowRepository showRepository, BookedSeatRepository bookedSeatRepository,
                       BookingRepository bookingRepository) {
        this.showRepository = showRepository;
        this.bookedSeatRepository = bookedSeatRepository;
        this.bookingRepository = bookingRepository;
    }

    @Transactional
    public ShowResponse create(ShowRequest req) {
        Show show = new Show();
        apply(show, req);
        return toResponse(showRepository.save(show));
    }

    @Transactional(readOnly = true)
    public List<ShowResponse> list(boolean upcomingOnly, String search) {
        List<Show> shows;
        if (search != null && !search.isBlank()) {
            shows = showRepository.findByTitleContainingIgnoreCaseOrderByShowTimeAsc(search.trim());
            if (upcomingOnly) {
                LocalDateTime now = LocalDateTime.now();
                shows = shows.stream().filter(s -> s.getShowTime().isAfter(now)).toList();
            }
        } else if (upcomingOnly) {
            shows = showRepository.findByShowTimeAfterOrderByShowTimeAsc(LocalDateTime.now());
        } else {
            shows = showRepository.findAll();
        }
        return shows.stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public ShowResponse get(Long id) {
        return toResponse(find(id));
    }

    @Transactional
    public ShowResponse update(Long id, ShowRequest req) {
        Show show = find(id);
        boolean layoutChanged = show.getTotalRows() != req.totalRows() || show.getSeatsPerRow() != req.seatsPerRow();
        if (layoutChanged && bookingRepository.existsByShowId(id)) {
            throw new ConflictException("Seat layout cannot be changed after bookings exist for this show");
        }
        apply(show, req);
        return toResponse(showRepository.save(show));
    }

    @Transactional
    public void delete(Long id) {
        Show show = find(id);
        if (bookingRepository.existsByShowId(id)) {
            throw new ConflictException("Show has bookings and cannot be deleted");
        }
        showRepository.delete(show);
    }

    @Transactional(readOnly = true)
    public SeatMapResponse seatMap(Long id) {
        Show show = find(id);
        Set<String> booked = new HashSet<>(bookedSeatRepository.findSeatLabelsByShowId(id));
        List<String> bookedSorted = new ArrayList<>();
        List<String> available = new ArrayList<>();
        for (int r = 0; r < show.getTotalRows(); r++) {
            for (int n = 1; n <= show.getSeatsPerRow(); n++) {
                String label = "" + (char) ('A' + r) + n;
                (booked.contains(label) ? bookedSorted : available).add(label);
            }
        }
        return new SeatMapResponse(show.getId(), show.getTitle(), show.getTotalRows(), show.getSeatsPerRow(),
                show.getCapacity(), available.size(), bookedSorted, available);
    }

    Show find(Long id) {
        return showRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Show not found with id " + id));
    }

    private ShowResponse toResponse(Show show) {
        return ShowResponse.from(show, bookedSeatRepository.countByShowId(show.getId()));
    }

    private void apply(Show show, ShowRequest req) {
        if (req.showTime().isBefore(LocalDateTime.now()) && show.getId() == null) {
            throw new BadRequestException("Show time must be in the future");
        }
        show.setTitle(req.title().trim());
        show.setDescription(req.description());
        show.setGenre(req.genre());
        show.setDurationMinutes(req.durationMinutes());
        show.setShowTime(req.showTime());
        show.setHall(req.hall().trim());
        show.setTicketPrice(req.ticketPrice());
        show.setTotalRows(req.totalRows());
        show.setSeatsPerRow(req.seatsPerRow());
    }
}
