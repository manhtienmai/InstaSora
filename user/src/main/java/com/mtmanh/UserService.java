package com.mtmanh;

import org.springframework.stereotype.Service;

@Service
public record UserService(UserRepository userRepository) {
    public void registerUser(UserRegistrationRequest userRegistrationRequest) {
        User user = User.builder()
                .firstName(userRegistrationRequest.firstName())
                .lastName(userRegistrationRequest.lastName())
                .email(userRegistrationRequest.email())
                .build();
        userRepository.save(user);
    }
}
