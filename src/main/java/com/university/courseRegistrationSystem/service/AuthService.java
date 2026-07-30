package com.university.courseRegistrationSystem.service;

import com.university.courseRegistrationSystem.dto.AuthResponse;
import com.university.courseRegistrationSystem.dto.LoginRequest;
import com.university.courseRegistrationSystem.model.User;
import com.university.courseRegistrationSystem.security.JWTService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JWTService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse login(LoginRequest request){

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()));

        User user = (User) authentication.getPrincipal();

        String token = jwtService.generateToken(user);

        return new AuthResponse(token,user.getRole().name());
    }

}
