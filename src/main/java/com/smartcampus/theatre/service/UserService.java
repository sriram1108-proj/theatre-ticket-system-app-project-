package com.smartcampus.theatre.service;

import com.smartcampus.theatre.dto.UserRequest;
import com.smartcampus.theatre.dto.UserResponse;
import com.smartcampus.theatre.exception.ConflictException;
import com.smartcampus.theatre.exception.ResourceNotFoundException;
import com.smartcampus.theatre.model.AppUser;
import com.smartcampus.theatre.repository.UserRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public UserResponse register(UserRequest req) {
        String email = req.email().trim().toLowerCase();
        if (userRepository.existsByEmail(email)) {
            throw new ConflictException("A user with this email already exists");
        }
        String campusId = (req.campusId() == null || req.campusId().isBlank()) ? null : req.campusId().trim();
        if (campusId != null && userRepository.existsByCampusId(campusId)) {
            throw new ConflictException("A user with this campus id already exists");
        }
        AppUser user = new AppUser();
        user.setName(req.name().trim());
        user.setEmail(email);
        user.setCampusId(campusId);
        return UserResponse.from(userRepository.save(user));
    }

    @Transactional(readOnly = true)
    public List<UserResponse> list() {
        return userRepository.findAll().stream().map(UserResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public UserResponse get(Long id) {
        return UserResponse.from(find(id));
    }

    AppUser find(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));
    }
}
