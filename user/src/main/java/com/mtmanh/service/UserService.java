package com.mtmanh.service;

import com.mtmanh.dto.AuthRequest;
import com.mtmanh.dto.AuthResponse;
import com.mtmanh.dto.UserDto;
import com.mtmanh.dto.UserRegistrationRequest;
import com.mtmanh.model.User;
import com.mtmanh.model.UserRole;
import com.mtmanh.repository.UserRepository;
import com.mtmanh.security.JwtService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;

    @Transactional
    public void registerUser(UserRegistrationRequest request) {
        // Check if email or username already exists
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalStateException("Email already taken");
        }
        
        if (userRepository.existsByUsername(request.username())) {
            throw new IllegalStateException("Username already taken");
        }
        
        // Create verification token
        String verificationToken = UUID.randomUUID().toString();
        
        // Create and save user
        User user = User.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .username(request.username())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(UserRole.USER)
                .verificationToken(verificationToken)
                .enabled(false)
                .build();
        
        userRepository.save(user);
        
        // Send verification email
        emailService.sendVerificationEmail(user.getEmail(), verificationToken);
    }
    
    @Transactional
    public void verifyEmail(String token) {
        User user = userRepository.findByVerificationToken(token)
                .orElseThrow(() -> new IllegalStateException("Invalid verification token"));
        
        user.setEnabled(true);
        user.setVerificationToken(null);
        userRepository.save(user);
    }
    
    public AuthResponse authenticate(AuthRequest request) {
        // Attempt authentication
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsernameOrEmail(),
                        request.getPassword()
                )
        );
        
        // Create JWT token if authentication successful
        String jwt = jwtService.generateToken(
                (org.springframework.security.core.userdetails.User) authentication.getPrincipal()
        );
        
        // Get user details
        User user = userRepository.findByEmail(request.getUsernameOrEmail())
                .orElseGet(() -> userRepository.findByUsername(request.getUsernameOrEmail())
                        .orElseThrow(() -> new UsernameNotFoundException("User not found")));
        
        return AuthResponse.builder()
                .token(jwt)
                .user(mapToUserDto(user))
                .build();
    }
    
    @Transactional
    public void forgotPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("User not found with email: " + email));
        
        String resetToken = UUID.randomUUID().toString();
        user.setResetPasswordToken(resetToken);
        user.setResetPasswordTokenExpiry(LocalDateTime.now().plusHours(1)); // Token valid for 1 hour
        userRepository.save(user);
        
        emailService.sendPasswordResetEmail(email, resetToken);
    }
    
    @Transactional
    public void resetPassword(String token, String newPassword) {
        User user = userRepository.findByResetPasswordToken(token)
                .orElseThrow(() -> new IllegalStateException("Invalid or expired reset token"));
        
        // Check if token is expired
        if (user.getResetPasswordTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Reset token has expired");
        }
        
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetPasswordToken(null);
        user.setResetPasswordTokenExpiry(null);
        userRepository.save(user);
    }
    
    public UserDto getUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("User not found"));
        
        return mapToUserDto(user);
    }
    
    @Transactional
    public UserDto updateUserProfile(Long userId, UserDto userDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("User not found"));
        
        // Update fields that are allowed to be changed
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setBio(userDto.getBio());
        user.setProfileImageUrl(userDto.getProfileImageUrl());
        user.setPrivate(userDto.isPrivate());
        
        // Save updated user
        User updatedUser = userRepository.save(user);
        
        return mapToUserDto(updatedUser);
    }
    
    @Transactional
    public void changePassword(Long userId, String currentPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("User not found"));
        
        // Verify current password
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new IllegalStateException("Current password is incorrect");
        }
        
        // Update password
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
    
    // Helper method to convert User entity to UserDto
    private UserDto mapToUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .username(user.getUsername())
                .email(user.getEmail())
                .bio(user.getBio())
                .profileImageUrl(user.getProfileImageUrl())
                .isPrivate(user.isPrivate())
                .role(user.getRole())
                .build();
    }
} 