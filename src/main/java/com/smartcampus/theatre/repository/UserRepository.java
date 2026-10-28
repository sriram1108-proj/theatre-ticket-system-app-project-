package com.smartcampus.theatre.repository;

import com.smartcampus.theatre.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<AppUser, Long> {
    boolean existsByEmail(String email);
    boolean existsByCampusId(String campusId);
}
