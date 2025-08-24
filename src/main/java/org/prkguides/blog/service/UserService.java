package org.prkguides.blog.service;

import org.prkguides.blog.dto.*;

import java.util.List;

public interface UserService {

    // User registration and profile management
    UserDto registerUser(UserRegistrationDto registrationDto);
    UserDto getCurrentUserProfile(String username);
    UserDto updateUserProfile(String username, UserUpdateDto userUpdateDto);
    void changePassword(String username, PasswordChangeDto passwordChangeDto);

    // Public user information
    UserSummaryDto getUserByUsername(String username);
    List<UserSummaryDto> getAuthors();

    // Admin operations
    PaginationResponse<UserSummaryDto> getAllUsers(int pageNo, int pageSize);
    UserDto updateUserRole(Long id, String role);
    UserDto toggleUserActive(Long id);
    void deleteUser(Long id);

    // Statistics
    Long getTotalUserCount();
    Long getActiveUserCount();
}
