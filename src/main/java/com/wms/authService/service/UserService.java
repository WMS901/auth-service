package com.wms.authService.service;

import com.wms.authService.dto.SignupRequestDto;
import com.wms.authService.dto.UserResponseDto;

public interface UserService {
    UserResponseDto signup(SignupRequestDto signupRequestDto);
}
