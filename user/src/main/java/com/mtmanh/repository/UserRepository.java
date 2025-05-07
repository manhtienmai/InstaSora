package com.mtmanh.repository;

import com.mtmanh.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByVerificationToken(String token);
    Optional<User> findByResetPasswordToken(String token);
    Optional<User> findByOauth2ProviderAndOauth2Id(String provider, String providerId);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
} 