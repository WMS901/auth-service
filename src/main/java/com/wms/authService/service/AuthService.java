package com.wms.authService.service;

import com.wms.authService.dto.LoginRequestDto;
import com.wms.authService.dto.TokenResponseDto;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;

public interface AuthService {
    ResponseEntity<TokenResponseDto> login(LoginRequestDto loginRequestDto, HttpServletResponse response);
}