package com.taaply.todo.auth;

import com.taaply.todo.auth.dto.AuthResponse;
import com.taaply.todo.auth.dto.GoogleAuthRequest;
import com.taaply.todo.users.UserEntity;
import com.taaply.todo.users.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

        private final UserService userService;
        private final JwtUtil jwtUtil;
        private final JwtConfig jwtConfig;

        @Transactional
        public AuthResponse authenticateGoogle(GoogleAuthRequest request) {
                UserEntity user = userService.findOrCreate(request.getEmail(), request.getName());

                String token = jwtUtil.generateToken(user.getId(), user.getEmail());

                Instant expiresAt = Instant.now().plusMillis(jwtConfig.getExpiration());

                log.info("User authenticated: {} ({})", user.getEmail(), user.getId());

                return AuthResponse.builder()
                                .user(AuthResponse.UserDto.builder()
                                                .id(user.getId())
                                                .email(user.getEmail())
                                                .name(user.getName())
                                                .createdAt(user.getCreatedAt())
                                                .build())
                                .accessToken(token)
                                .tokenType("Bearer")
                                .expiresAt(expiresAt)
                                .build();
        }

        @Transactional(readOnly = true)
        public AuthResponse getCurrentUser(java.util.UUID userId) {
                UserEntity user = userService.findById(userId);

                return AuthResponse.builder()
                                .user(AuthResponse.UserDto.builder()
                                                .id(user.getId())
                                                .email(user.getEmail())
                                                .name(user.getName())
                                                .createdAt(user.getCreatedAt())
                                                .build())
                                .build();
        }
}
