package org.project.multitenantauthservice.controller;

import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.project.multitenantauthservice.entity.ApiResponse;
import org.project.multitenantauthservice.entity.dto.request.AuthRequest;
import org.project.multitenantauthservice.entity.dto.request.UserRequest;
import org.project.multitenantauthservice.entity.dto.response.AuthResponse;
import org.project.multitenantauthservice.entity.dto.response.UserResponse;
import org.project.multitenantauthservice.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> createUser(@RequestBody UserRequest request)
            throws BadRequestException {
        UserResponse user = userService.createUser(request);
        ApiResponse<UserResponse> response = ApiResponse.<UserResponse>builder()
                .status("success")
                .message("Successfully create user")
                .data(user)
                .timestamp(Instant.now())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@RequestBody AuthRequest request) {
        AuthResponse authResponse = userService.login(request);

        ApiResponse<AuthResponse> response = ApiResponse.<AuthResponse>builder()
                .message("Login berhasil")
                .data(authResponse)
                .status("success")
                .timestamp(Instant.now())
                .build();

        return ResponseEntity.ok(response);
    }
}
