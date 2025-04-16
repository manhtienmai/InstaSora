package com.mtmanh;

public record UserRegistrationRequest(
        String firstName,
        String lastName,
        String email
) {
}
