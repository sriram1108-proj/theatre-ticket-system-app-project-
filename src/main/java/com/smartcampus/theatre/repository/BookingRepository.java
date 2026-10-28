package com.smartcampus.theatre.repository;

import com.smartcampus.theatre.model.Booking;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    boolean existsByShowId(Long showId);

    @Query("select distinct b from Booking b join fetch b.show join fetch b.user left join fetch b.seats "
            + "where b.user.id = :userId order by b.createdAt desc")
    List<Booking> findByUserId(@Param("userId") Long userId);

    @Query("select b from Booking b join fetch b.show join fetch b.user left join fetch b.seats "
            + "where b.bookingCode = :code")
    Optional<Booking> findByBookingCode(@Param("code") String code);

    @Query("select b from Booking b join fetch b.show join fetch b.user left join fetch b.seats "
            + "where b.id = :id")
    Optional<Booking> findDetailedById(@Param("id") Long id);

    @Query("select distinct b from Booking b join fetch b.show join fetch b.user left join fetch b.seats "
            + "where b.show.id = :showId order by b.createdAt desc")
    List<Booking> findByShowId(@Param("showId") Long showId);
}
