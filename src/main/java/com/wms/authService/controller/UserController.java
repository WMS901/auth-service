package com.wms.authService.controller;

import com.wms.authService.dto.SignupRequestDto;
import com.wms.authService.dto.UserResponseDto;
import com.wms.authService.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<UserResponseDto> signup(@Valid @RequestBody SignupRequestDto signupRequestDto) {
        UserResponseDto response = userService.signup(signupRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
