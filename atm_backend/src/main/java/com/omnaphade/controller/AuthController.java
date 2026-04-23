package com.omnaphade.controller;

import com.omnaphade.dtos.LoginRequest;
import com.omnaphade.dtos.LoginResponse;
import com.omnaphade.dtos.RefreshTokenRequest;
import com.omnaphade.security.jwt.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.accountNumber(), request.pin())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtUtil.generateToken(request.accountNumber());
        String refreshToken = jwtUtil.generateRefreshToken(request.accountNumber());

        LoginResponse response = new LoginResponse(token, refreshToken, "Login successful");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        String refreshToken = request.refreshToken();

        if (jwtUtil.validateToken(refreshToken) && !jwtUtil.isTokenExpired(refreshToken)) {
            String accountNumber = jwtUtil.getSubjectFromToken(refreshToken);
            String newToken = jwtUtil.generateToken(accountNumber);
            String newRefreshToken = jwtUtil.generateRefreshToken(accountNumber);

            LoginResponse response = new LoginResponse(newToken, newRefreshToken, "Token refreshed");
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(new LoginResponse(null, null, "Invalid refresh token"));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok("Logged out successfully");
    }
}
