package com.mtmanh.config;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

@Configuration
public class DotenvConfig {
    
    private static final Logger logger = LoggerFactory.getLogger(DotenvConfig.class);
    
    @PostConstruct
    public void loadEnv() {
        File envFile = new File(".env");
        
        if (envFile.exists()) {
            logger.info("Loading environment variables from .env file");
            
            Dotenv dotenv = Dotenv.configure()
                    .directory(".")
                    .ignoreIfMalformed()
                    .ignoreIfMissing()
                    .load();
            
            // Set environment variables if they don't already exist
            dotenv.entries().forEach(entry -> {
                if (System.getenv(entry.getKey()) == null) {
                    System.setProperty(entry.getKey(), entry.getValue());
                }
            });
            
            logger.info(".env file loaded successfully");
        } else {
            logger.warn(".env file not found. Using system environment variables.");
        }
    }
} 