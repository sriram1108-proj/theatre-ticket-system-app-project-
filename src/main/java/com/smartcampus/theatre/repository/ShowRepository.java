package com.smartcampus.theatre.repository;

import com.smartcampus.theatre.model.Show;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShowRepository extends JpaRepository<Show, Long> {
    List<Show> findByShowTimeAfterOrderByShowTimeAsc(LocalDateTime time);
    List<Show> findByTitleContainingIgnoreCaseOrderByShowTimeAsc(String title);
}
