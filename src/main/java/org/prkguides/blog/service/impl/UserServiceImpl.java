package org.prkguides.blog.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.prkguides.blog.dto.*;
import org.prkguides.blog.entity.User;
import org.prkguides.blog.enums.PostStatus;
import org.prkguides.blog.enums.Role;
import org.prkguides.blog.exceptions.ResourceNotFoundException;
import org.prkguides.blog.repository.PostRepository;
import org.prkguides.blog.repository.UserRepository;
import org.prkguides.blog.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final PostRepository postRepository;


    @Override
    public UserDto registerUser(UserRegistrationDto registrationDto) {

        log.info("Registering new user: {}" , registrationDto.getUsername());

        //Check if username already exists
        if(userRepository.existsByUsername(registrationDto.getUsername())){
            throw new RuntimeException("Username is already taken");
        }

        //Check if email already exists
        if(userRepository.existsByEmail(registrationDto.getEmail())){
            throw new RuntimeException("Email is already registered");
        }

        User user = new User();

        user.setUsername(registrationDto.getUsername());
        user.setEmail(registrationDto.getEmail());
        user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        user.setFirstName(registrationDto.getFirstName());
        user.setLastName(registrationDto.getLastName());
        user.setBio(registrationDto.getBio());
        user.setWebsiteUrl(registrationDto.getWebsiteUrl());
        user.setRole(Role.USER);
        user.setIsActive(true);

        User savedUser = userRepository.save(user);

        log.info("User registered successfully with Id: {}", savedUser.getId());

        return mapUserEntityToDto(savedUser);
    }

    public UserDto getCurrentUserProfile(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        return mapEntityToDto(user);
    }

    @Override
    public UserDto updateUserProfile(String username, UserUpdateDto userUpdateDto) {
        log.info("Updating profile for user: {}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        // Check if email is being changed and if it's already taken
        if (userUpdateDto.getEmail() != null &&
                !userUpdateDto.getEmail().equals(user.getEmail()) &&
                userRepository.existsByEmail(userUpdateDto.getEmail())) {
            throw new RuntimeException("Email is already registered!");
        }

        // Update fields
        if (userUpdateDto.getEmail() != null) {
            user.setEmail(userUpdateDto.getEmail());
        }
        if (userUpdateDto.getFirstName() != null) {
            user.setFirstName(userUpdateDto.getFirstName());
        }
        if (userUpdateDto.getLastName() != null) {
            user.setLastName(userUpdateDto.getLastName());
        }
        if (userUpdateDto.getBio() != null) {
            user.setBio(userUpdateDto.getBio());
        }
        if (userUpdateDto.getAvatarUrl() != null) {
            user.setAvatarUrl(userUpdateDto.getAvatarUrl());
        }
        if (userUpdateDto.getWebsiteUrl() != null) {
            user.setWebsiteUrl(userUpdateDto.getWebsiteUrl());
        }

        User savedUser = userRepository.save(user);
        log.info("Profile updated successfully for user: {}", username);

        return mapEntityToDto(savedUser);
    }

    @Override
    public void changePassword(String username, PasswordChangeDto passwordChangeDto) {
        log.info("Password change attempt for user: {}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        // Verify current password
        if (!passwordEncoder.matches(passwordChangeDto.getCurrentPassword(), user.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }

        // Verify new password confirmation
        if (!passwordChangeDto.getNewPassword().equals(passwordChangeDto.getConfirmPassword())) {
            throw new RuntimeException("New password and confirmation do not match");
        }

        // Update password
        user.setPassword(passwordEncoder.encode(passwordChangeDto.getNewPassword()));
        userRepository.save(user);

        log.info("Password changed successfully for user: {}", username);
    }

    @Override
    @Transactional(readOnly = true)
    public UserSummaryDto getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        return modelMapper.map(user, UserSummaryDto.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserSummaryDto> getAuthors() {
        // Get users who have published posts
        List<User> authors = userRepository.findByIsActiveTrue().stream()
                .filter(user -> !user.getPosts().isEmpty())
                .collect(Collectors.toList());

        return authors.stream()
                .map(user -> modelMapper.map(user, UserSummaryDto.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PaginationResponse<UserSummaryDto> getAllUsers(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by("createdAt").descending());
        Page<User> usersPage = userRepository.findAll(pageable);

        List<UserSummaryDto> content = usersPage.getContent().stream()
                .map(user -> modelMapper.map(user, UserSummaryDto.class))
                .collect(Collectors.toList());

        PaginationResponse<UserSummaryDto> response = new PaginationResponse<>();
        response.setContent(content);
        response.setPage(usersPage.getNumber());
        response.setSize(usersPage.getSize());
        response.setTotalPages(usersPage.getTotalPages());
        response.setTotalElements(usersPage.getTotalElements());
        response.setLast(usersPage.isLast());
        response.setFirst(usersPage.isFirst());
        response.setEmpty(usersPage.isEmpty());

        return response;
    }

    @Override
    public UserDto updateUserRole(Long id, String roleString) {
        log.info("Updating role for user ID: {} to {}", id, roleString);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id.toString()));

        try {
            Role role = Role.valueOf(roleString.toUpperCase());
            user.setRole(role);
            User savedUser = userRepository.save(user);

            log.info("Role updated successfully for user ID: {}", id);
            return mapEntityToDto(savedUser);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid role: " + roleString);
        }
    }

    @Override
    public UserDto toggleUserActive(Long id) {
        log.info("Toggling active status for user ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id.toString()));

        user.setIsActive(!user.getIsActive());
        User savedUser = userRepository.save(user);

        log.info("Active status toggled for user ID: {} to {}", id, savedUser.getIsActive());
        return mapEntityToDto(savedUser);
    }

    @Override
    public void deleteUser(Long id) {
        log.info("Deleting user with ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id.toString()));

        userRepository.delete(user);
        log.info("User deleted successfully with ID: {}", id);
    }

    //Todo : Move to mapper class
    private UserDto mapUserEntityToDto(User user){
        UserDto dto = modelMapper.map(user,UserDto.class);
        dto.setFullName(user.getFullName());

        //Get post count for this user
        Long postCount = postRepository.countByAuthorAndStatus(user.getId(), PostStatus.PUBLISHED);

        dto.setPostCount(postCount);
        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public Long getTotalUserCount() {
        return userRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Long getActiveUserCount() {
        return (long) userRepository.findByIsActiveTrue().size();
    }

    private UserDto mapEntityToDto(User user) {
        UserDto dto = modelMapper.map(user, UserDto.class);
        dto.setFullName(user.getFullName());

        // Get post count for this user
        Long postCount = postRepository.countByAuthorAndStatus(user.getId(),
                org.prkguides.blog.enums.PostStatus.PUBLISHED);
        dto.setPostCount(postCount);

        return dto;
    }
}
