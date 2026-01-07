package com.taaply.todo.auth;

import com.taaply.todo.auth.dto.AuthResponse;
import com.taaply.todo.auth.dto.GoogleAuthRequest;
import com.taaply.todo.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/google")
    public ResponseEntity<ApiResponse<AuthResponse>> authenticateGoogle(
            @Valid @RequestBody GoogleAuthRequest request) {

        AuthResponse response = authService.authenticateGoogle(request);

        return ResponseEntity.ok(ApiResponse.success("Authentication successful", response));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<AuthResponse>> getMe(
            @org.springframework.security.core.annotation.AuthenticationPrincipal com.taaply.todo.auth.UserPrincipal principal) {

        AuthResponse response = authService.getCurrentUser(principal.getUserId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
