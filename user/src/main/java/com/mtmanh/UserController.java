package com.mtmanh;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/user")
@Slf4j
public record UserController(UserService userService) {

    @PostMapping
    public void registerUser(@RequestBody UserRegistrationRequest userRegistrationRequest) {
        log.info("Registering user: {}", userRegistrationRequest);
        userService.registerUser(userRegistrationRequest);
    }
}
