package org.prkguides.blog.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.prkguides.blog.dto.APIResponse;
import org.prkguides.blog.dto.JwtResponseDto;
import org.prkguides.blog.dto.LoginRequestDto;
import org.prkguides.blog.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = {"http://localhost:4200", "https://yourdomain.com"})
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication management endpoints")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "User login", description = "Authenticate user and return JWT token")
    @PostMapping("/signin")
    public ResponseEntity<APIResponse<JwtResponseDto>> authenticateUser(@Valid @RequestBody LoginRequestDto loginRequest){

        log.info("Login attempt for username: {}", loginRequest.getUsername());

        JwtResponseDto jwtResponse = authService.authenticateUser(loginRequest);

        return ResponseEntity.ok(APIResponse.success("User authenticated successfully", jwtResponse));

    }

    @Operation(summary = "Token Validation", description = "JWT token validation")
    @PostMapping("/validate")
    public ResponseEntity<APIResponse<Boolean>> validateToken(@RequestParam String token){
        boolean isValid = authService.validateToken(token);
        return ResponseEntity.ok(APIResponse.success("Token Validation completed",isValid));
    }
}
