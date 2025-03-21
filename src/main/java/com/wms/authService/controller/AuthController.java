package com.wms.authService.controller;

import com.wms.authService.dto.LoginRequestDto;
import com.wms.authService.dto.TokenResponseDto;
import com.wms.authService.service.AuthService;
import com.wms.authService.config.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<TokenResponseDto> login(@Valid @RequestBody LoginRequestDto loginRequestDto, HttpServletResponse response) {
        return authService.login(loginRequestDto, response);
    }

    @GetMapping("/validateToken")
    public ResponseEntity<?> validateToken(@RequestParam String token) {
        String email = jwtUtil.validateTokenAndGetEmail(token);

        if ("EXPIRED".equals(email)) {
            return ResponseEntity.status(401).body("❌ 토큰이 만료되었습니다.");
        } else if ("INVALID".equals(email)) {
            return ResponseEntity.status(401).body("❌ 유효하지 않은 토큰입니다.");
        } else {
            return ResponseEntity.ok("✅ 유효한 토큰입니다. 사용자: " + email);
        }
    }
}
