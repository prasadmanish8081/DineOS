package com.dineos.service.impl;

import com.dineos.dto.request.LoginRequest;
import com.dineos.dto.request.RegisterRequest;
import com.dineos.dto.response.AuthResponse;
import com.dineos.dto.response.UserResponse;
import com.dineos.entity.User;
import com.dineos.enums.Role;
import com.dineos.exception.ResourceAlreadyExistsException;
import com.dineos.exception.InvalidCredentialsException;
import com.dineos.exception.ResourceNotFoundException;
import com.dineos.repository.UserRepository;
import com.dineos.service.AuthService;
import com.dineos.util.JwtTokenUtil;
import com.dineos.util.UserMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;

    public AuthServiceImpl(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtTokenUtil jwtTokenUtil
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmailIgnoreCase(request.email())) {
            throw new ResourceAlreadyExistsException("Email is already registered");
        }

        User user = new User();
        user.setName(request.name().trim());
        user.setEmail(request.email().trim().toLowerCase());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(resolveSelfRegistrationRole(request.role()));
        user = userRepository.save(user);

        Instant expiresAt = jwtTokenUtil.getExpirationFromNow();
        String token = jwtTokenUtil.generateToken(user, expiresAt);
        return buildAuthResponse(token, expiresAt, user);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.email().trim().toLowerCase(),
                            request.password()
                    )
            );
        } catch (AuthenticationException ex) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        String email = authentication.getName();
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Instant expiresAt = jwtTokenUtil.getExpirationFromNow();
        String token = jwtTokenUtil.generateToken(user, expiresAt);
        return buildAuthResponse(token, expiresAt, user);
    }

    private AuthResponse buildAuthResponse(String token, Instant expiresAt, User user) {
        UserResponse userResponse = UserMapper.toResponse(user);
        return new AuthResponse(token, "Bearer", expiresAt, userResponse);
    }

    private Role resolveSelfRegistrationRole(Role requestedRole) {
        if (requestedRole == null) {
            return Role.OWNER;
        }
        if (requestedRole == Role.OWNER || requestedRole == Role.CUSTOMER) {
            return requestedRole;
        }
        return Role.CUSTOMER;
    }
}
