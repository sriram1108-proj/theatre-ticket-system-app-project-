package com.smartcampus.theatre.repository;

import com.smartcampus.theatre.model.BookedSeat;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookedSeatRepository extends JpaRepository<BookedSeat, Long> {

    @Query("select s.seatLabel from BookedSeat s where s.show.id = :showId")
    List<String> findSeatLabelsByShowId(@Param("showId") Long showId);

    @Query("select s.seatLabel from BookedSeat s where s.show.id = :showId and s.seatLabel in :labels")
    List<String> findTakenAmong(@Param("showId") Long showId, @Param("labels") Collection<String> labels);

    long countByShowId(Long showId);
}
