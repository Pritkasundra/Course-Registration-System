package com.university.courseRegistrationSystem.service;

import com.university.courseRegistrationSystem.dto.AuthResponse;
import com.university.courseRegistrationSystem.dto.LoginRequest;
import com.university.courseRegistrationSystem.model.User;
import com.university.courseRegistrationSystem.repository.UserRepository;
import com.university.courseRegistrationSystem.security.JWTService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JWTService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse login(LoginRequest request){
        if(request.getUsername() == null || request.getPassword() == null){
            throw new RuntimeException("Invalid Username or Password");
        }

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()));

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(()->new RuntimeException("User Not Found"));

        String token = jwtService.generateToken(user);

        return new AuthResponse(token,user.getRole().name());
    }

}
