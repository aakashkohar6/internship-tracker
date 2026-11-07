package com.internshiptracker;

import org.springframework.stereotype.Service;

import java.sql.SQLException;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordService passwordService;

    public UserService(UserRepository userRepository, PasswordService passwordService) {
        this.userRepository = userRepository;
        this.passwordService = passwordService;
    }

    public void register(String username, String rawPassword) throws SQLException {
        if(username == null || username.isBlank()){
            throw new IllegalArgumentException("Username must not be blank");
        }

        if(rawPassword == null || rawPassword.isBlank())
        {
            throw new IllegalArgumentException("Password cannot be blank");
        }

        String passwordHash = passwordService.hashPassword(rawPassword);
        userRepository.save(username,passwordHash);
    }

    public User login(String username, String rawPassword) throws SQLException {
        if(username == null || username.isBlank()
        || rawPassword == null || rawPassword.isBlank()){
            throw new InvalidCredentialsException("Invalid Username or Password");
        }
        User user = userRepository.findByUsername(username);

        if (user == null) {
            throw new InvalidCredentialsException("Invalid Username or Password");
        }

        if(!passwordService.verifyPassword(rawPassword, user.getPasswordHash() )){
            throw new InvalidCredentialsException("Invalid Username or Password");
        }
        return user;
    }
}
