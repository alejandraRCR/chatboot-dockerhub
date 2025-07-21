package com.chatboot.controller;

import com.chatboot.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthService service;

    @PostMapping("/register")
    public ResponseEntity<TokenResponse> register(@RequestBody RegisterRequest request) {
        final TokenResponse response = service.register(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> authenticate(@RequestBody AuthRequest request) {
        final TokenResponse response = service.authenticate(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/check-status")
    public ResponseEntity<?> checkStatus(HttpServletRequest request) {
        String token = service.extractTokenFromRequest(request);

        if (token == null || token.isBlank()) {
            return ResponseEntity.ok(new AuthResponse(false, "No token detected"));
        }

        try {
            AuthResponse response = service.checkTokenStatus(token);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.ok(new AuthResponse(false, "Invalid or expired token"));
        }
    }

    @PostMapping("/refresh")
    public TokenResponse refreshToken(
            @RequestHeader(HttpHeaders.AUTHORIZATION) final String authentication
    ) {
        return service.refreshToken(authentication);
    }
}
