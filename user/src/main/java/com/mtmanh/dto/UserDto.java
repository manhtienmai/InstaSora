package com.mtmanh.dto;

import com.mtmanh.model.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private String bio;
    private String profileImageUrl;
    private boolean isPrivate;
    private UserRole role;
} 