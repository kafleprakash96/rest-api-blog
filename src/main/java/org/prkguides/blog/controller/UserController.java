package org.prkguides.blog.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.prkguides.blog.dto.APIResponse;
import org.prkguides.blog.dto.UserDto;
import org.prkguides.blog.dto.UserRegistrationDto;
import org.prkguides.blog.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@CrossOrigin(origins = {"http://localhost:4200"})
@RequiredArgsConstructor
@Tag(name = "users", description = "User management endpoints")
public class UserController {

    private final UserService userService;

    @Operation(summary = "Register new user", description = "Register new user account")
    @PostMapping("/register")
    public ResponseEntity<APIResponse<UserDto>> registerUser(
            @Valid @RequestBody UserRegistrationDto registrationDto
            ){
        log.info("User registration attempt for username: {}", registrationDto.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(
                APIResponse.success(
                        "User registered successfully", userService.registerUser(registrationDto)));
    }

}
