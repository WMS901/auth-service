package com.wms.authService.service.impl;

import com.wms.authService.config.JwtUtil;
import com.wms.authService.dto.LoginRequestDto;
import com.wms.authService.dto.TokenResponseDto;
import com.wms.authService.entity.User;
import com.wms.authService.repository.UserRepository;
import com.wms.authService.service.AuthService;
import com.wms.authService.config.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    public ResponseEntity<TokenResponseDto> login(LoginRequestDto loginRequestDto, HttpServletResponse response) {
        Optional<User> userOptional = userRepository.findByEmail(loginRequestDto.getEmail());

        if (userOptional.isEmpty() || !passwordEncoder.matches(loginRequestDto.getPassword(), userOptional.get().getPassword())) {
            return ResponseEntity.status(401).body(new TokenResponseDto(null, null, "Invalid email or password"));
        }

        User user = userOptional.get();
        String accessToken = jwtUtil.createAccessToken(user.getEmail());
        String refreshToken = jwtUtil.createRefreshToken(user.getEmail()); // Redis에 저장됨

        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(7 * 24 * 60 * 60); // 7일

        response.addCookie(refreshTokenCookie);

        return ResponseEntity.ok(new TokenResponseDto(accessToken, null, "Login successful"));
    }
}
