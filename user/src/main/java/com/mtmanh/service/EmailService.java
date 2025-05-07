package com.mtmanh.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    
    @Value("${spring.mail.username}")
    private String fromEmail;
    
    @Value("${application.base-url}")
    private String baseUrl;

    @Async
    public void sendVerificationEmail(String toEmail, String token) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            
            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Email Verification");
            
            String verificationUrl = baseUrl + "/api/v1/user/verify-email?token=" + token;
            String emailContent = 
                    "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px;'>" +
                    "<h2 style='color: #333;'>Welcome to InstaSora!</h2>" +
                    "<p>Thank you for registering. Please verify your email address to complete your registration.</p>" +
                    "<p><a href='" + verificationUrl + "' style='display: inline-block; background-color: #4CAF50; color: white; padding: 10px 20px; text-decoration: none; border-radius: 4px;'>Verify Email</a></p>" +
                    "<p>If the button above doesn't work, copy and paste the following link into your browser:</p>" +
                    "<p>" + verificationUrl + "</p>" +
                    "<p>This link will expire in 24 hours.</p>" +
                    "<p>If you did not create an account, please ignore this email.</p>" +
                    "</div>";
            
            helper.setText(emailContent, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send verification email", e);
        }
    }

    @Async
    public void sendPasswordResetEmail(String toEmail, String token) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            
            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Password Reset Request");
            
            String resetUrl = baseUrl + "/reset-password?token=" + token;
            String emailContent = 
                    "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px;'>" +
                    "<h2 style='color: #333;'>Password Reset Request</h2>" +
                    "<p>We received a request to reset your password. Click the button below to create a new password:</p>" +
                    "<p><a href='" + resetUrl + "' style='display: inline-block; background-color: #4CAF50; color: white; padding: 10px 20px; text-decoration: none; border-radius: 4px;'>Reset Password</a></p>" +
                    "<p>If the button above doesn't work, copy and paste the following link into your browser:</p>" +
                    "<p>" + resetUrl + "</p>" +
                    "<p>This link will expire in 1 hour.</p>" +
                    "<p>If you did not request a password reset, please ignore this email.</p>" +
                    "</div>";
            
            helper.setText(emailContent, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send password reset email", e);
        }
    }
} 