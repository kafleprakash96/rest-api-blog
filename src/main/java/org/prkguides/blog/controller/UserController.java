package org.prkguides.blog.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.prkguides.blog.dto.*;
import org.prkguides.blog.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @Operation(summary = "Get current user profile", description = "Get the authenticated user's profile")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/profile")
    public ResponseEntity<APIResponse<UserDto>> getCurrentUserProfile(Authentication authentication) {

        UserDto user = userService.getCurrentUserProfile(authentication.getName());
        return ResponseEntity.ok(APIResponse.success("Profile retrieved successfully", user));
    }

    @Operation(summary = "Update user profile", description = "Update the authenticated user's profile")
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/profile")
    public ResponseEntity<APIResponse<UserDto>> updateUserProfile(
            @Valid @RequestBody UserUpdateDto userUpdateDto,
            Authentication authentication) {

        log.info("Profile update attempt for user: {}", authentication.getName());
        UserDto updatedUser = userService.updateUserProfile(authentication.getName(), userUpdateDto);

        return ResponseEntity.ok(APIResponse.success("Profile updated successfully", updatedUser));
    }

    @Operation(summary = "Change password", description = "Change the authenticated user's password")
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/change-password")
    public ResponseEntity<APIResponse<String>> changePassword(
            @Valid @RequestBody PasswordChangeDto passwordChangeDto,
            Authentication authentication) {

        log.info("Password change attempt for user: {}", authentication.getName());
        userService.changePassword(authentication.getName(), passwordChangeDto);

        return ResponseEntity.ok(APIResponse.success("Password changed successfully", "Password updated"));
    }

    //Todo : Test with admin privileges
    @Operation(summary = "Get user by username", description = "Get public user information by username")
    @GetMapping("/{username}")
    public ResponseEntity<APIResponse<UserSummaryDto>> getUserByUsername(
            @Parameter(description = "Username") @PathVariable String username) {

        UserSummaryDto user = userService.getUserByUsername(username);
        return ResponseEntity.ok(APIResponse.success("User retrieved successfully", user));
    }

    @Operation(summary = "Get all users", description = "Get all users (Admin only)")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<APIResponse<PaginationResponse<UserSummaryDto>>> getAllUsers(
            @RequestParam(value = "pageNo", defaultValue = "0") int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {

        PaginationResponse<UserSummaryDto> users = userService.getAllUsers(pageNo, pageSize);
        return ResponseEntity.ok(APIResponse.success("Users retrieved successfully", users));
    }

    @Operation(summary = "Update user role", description = "Update user role (Admin only)")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/role")
    public ResponseEntity<APIResponse<UserDto>> updateUserRole(
            @PathVariable Long id,
            @RequestParam String role) {

        log.info("Role update attempt for user ID: {} to role: {}", id, role);
        UserDto updatedUser = userService.updateUserRole(id, role);

        return ResponseEntity.ok(APIResponse.success("User role updated successfully", updatedUser));
    }

    @Operation(summary = "Toggle user active status", description = "Activate or deactivate user (Admin only)")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/toggle-active")
    public ResponseEntity<APIResponse<UserDto>> toggleUserActive(@PathVariable Long id) {

        log.info("Toggle active status for user ID: {}", id);
        UserDto updatedUser = userService.toggleUserActive(id);

        return ResponseEntity.ok(APIResponse.success("User status updated successfully", updatedUser));
    }

    @Operation(summary = "Delete user", description = "Delete user account (Admin only)")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<APIResponse<String>> deleteUser(@PathVariable Long id) {

        log.info("Delete user attempt for ID: {}", id);
        userService.deleteUser(id);

        return ResponseEntity.ok(APIResponse.success("User deleted successfully",
                "User with ID " + id + " has been deleted"));
    }

    @Operation(summary = "Get authors", description = "Get all users who have published posts")
    @GetMapping("/authors")
    public ResponseEntity<APIResponse<List<UserSummaryDto>>> getAuthors() {

        List<UserSummaryDto> authors = userService.getAuthors();
        return ResponseEntity.ok(APIResponse.success("Authors retrieved successfully", authors));
    }
}
