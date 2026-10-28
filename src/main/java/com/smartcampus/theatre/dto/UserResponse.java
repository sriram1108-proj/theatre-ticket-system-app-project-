package com.smartcampus.theatre.dto;

import com.smartcampus.theatre.model.AppUser;

public record UserResponse(Long id, String name, String email, String campusId) {
    public static UserResponse from(AppUser u) {
        return new UserResponse(u.getId(), u.getName(), u.getEmail(), u.getCampusId());
    }
}
