package com.mtmanh;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<UserDto> findByUsername(String username);
    Optional<UserResponse> findByEmail(String email);
}
