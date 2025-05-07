package com.mtmanh.security;

import com.mtmanh.model.User;
import com.mtmanh.model.UserRole;
import com.mtmanh.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, 
                                        HttpServletResponse response, 
                                        Authentication authentication) throws IOException, ServletException {
        
        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        OAuth2User oAuth2User = oauthToken.getPrincipal();
        String provider = oauthToken.getAuthorizedClientRegistrationId();
        
        // Extract user info based on provider
        String providerId;
        String email;
        String firstName;
        String lastName;
        
        if ("google".equals(provider)) {
            providerId = oAuth2User.getAttribute("sub");
            email = oAuth2User.getAttribute("email");
            firstName = oAuth2User.getAttribute("given_name");
            lastName = oAuth2User.getAttribute("family_name");
        } else if ("github".equals(provider)) {
            providerId = oAuth2User.getAttribute("id").toString();
            email = oAuth2User.getAttribute("email");
            String name = oAuth2User.getAttribute("name");
            if (name != null && name.contains(" ")) {
                firstName = name.split(" ")[0];
                lastName = name.split(" ")[1];
            } else {
                firstName = name;
                lastName = "";
            }
        } else {
            Map<String, Object> attributes = oAuth2User.getAttributes();
            providerId = attributes.get("id").toString();
            email = (String) attributes.get("email");
            firstName = (String) attributes.getOrDefault("first_name", "");
            lastName = (String) attributes.getOrDefault("last_name", "");
        }
        
        // Check if user already exists with this OAuth provider
        Optional<User> existingUser = userRepository.findByOauth2ProviderAndOauth2Id(provider, providerId);
        User user;
        
        if (existingUser.isPresent()) {
            user = existingUser.get();
        } else if (email != null && userRepository.findByEmail(email).isPresent()) {
            // If user exists with the same email, link the OAuth account
            user = userRepository.findByEmail(email).get();
            user.setOauth2Provider(provider);
            user.setOauth2Id(providerId);
            userRepository.save(user);
        } else {
            // Create new user
            String username = createUniqueUsername(email != null ? email.split("@")[0] : "user");
            
            user = User.builder()
                    .email(email)
                    .username(username)
                    .firstName(firstName != null ? firstName : "")
                    .lastName(lastName != null ? lastName : "")
                    .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                    .oauth2Provider(provider)
                    .oauth2Id(providerId)
                    .role(UserRole.USER)
                    .enabled(true)
                    .build();
            
            userRepository.save(user);
        }
        
        // Generate JWT token
        String token = jwtService.generateToken(
                new org.springframework.security.core.userdetails.User(
                        user.getEmail(),
                        user.getPassword(),
                        java.util.Collections.emptyList()
                )
        );
        
        // Redirect with token
        getRedirectStrategy().sendRedirect(request, response, "/oauth2/redirect?token=" + token);
    }
    
    private String createUniqueUsername(String baseUsername) {
        String username = baseUsername;
        int counter = 0;
        
        while (userRepository.existsByUsername(username)) {
            counter++;
            username = baseUsername + counter;
        }
        
        return username;
    }
} 