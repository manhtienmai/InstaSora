# User Service

This microservice handles user authentication, registration, profile management, and OAuth2 integration.

## Configuration with .env

The service uses a `.env` file for configuration. A template file `.env` is provided in the root directory.

1. Edit the `.env` file and set the following variables:

```
# Google OAuth2 variables
GOOGLE_CLIENT_ID=your_google_client_id
GOOGLE_CLIENT_SECRET=your_google_client_secret

# GitHub OAuth2 variables
GITHUB_CLIENT_ID=your_github_client_id
GITHUB_CLIENT_SECRET=your_github_client_secret

# Email configuration
EMAIL_USERNAME=your_email@gmail.com
EMAIL_PASSWORD=your_email_password

# JWT Secret (Optional)
JWT_SECRET_KEY=your_jwt_secret_key
```

## Prerequisites

- Java 17
- Maven
- PostgreSQL database
- Eureka Server running on port 8761

## Database Setup

1. Make sure PostgreSQL is running
2. Create a database named `user`:
   ```sql
   CREATE DATABASE user;
   ```

## Running the Service

1. Make sure the Eureka Server is running
2. Run the service:
   ```
   mvn spring-boot:run
   ```

## Features

- User Registration with Email Verification
- Login with Username/Email and Password
- OAuth2 Authentication (Google, GitHub)
- Password Management (Reset, Change)
- Profile Management
- JWT-based Authentication

## API Endpoints

### Authentication
- POST `/api/v1/user/register` - Register a new user
- POST `/api/v1/user/login` - Login
- GET `/api/v1/user/verify-email?token={token}` - Verify email
- POST `/api/v1/user/forgot-password` - Request password reset
- POST `/api/v1/user/reset-password` - Reset password

### Profile Management
- GET `/api/v1/user/profile` - Get user profile
- PUT `/api/v1/user/profile` - Update user profile
- POST `/api/v1/user/change-password` - Change password

## Docker Support

This service can be run with Docker using the provided docker-compose.yml in the root directory. 