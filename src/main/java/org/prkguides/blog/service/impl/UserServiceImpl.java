package org.prkguides.blog.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.prkguides.blog.dto.*;
import org.prkguides.blog.entity.User;
import org.prkguides.blog.enums.PostStatus;
import org.prkguides.blog.enums.Role;
import org.prkguides.blog.repository.PostRepository;
import org.prkguides.blog.repository.UserRepository;
import org.prkguides.blog.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    @Override
    public UserDto getCurrentUserProfile(String username) {
        return null;
    }

    @Override
    public UserDto updateUserProfile(String username, UserUpdateDto userUpdateDto) {
        return null;
    }

    @Override
    public void changePassword(String username, PasswordChangeDto passwordChangeDto) {

    }

    @Override
    public UserSummaryDto getUserByUsername(String username) {
        return null;
    }

    @Override
    public List<UserSummaryDto> getAuthors() {
        return null;
    }

    @Override
    public PaginationResponse<UserSummaryDto> getAllUsers(int pageNo, int pageSize) {
        return null;
    }

    @Override
    public UserDto updateUserRole(Long id, String role) {
        return null;
    }

    @Override
    public UserDto toggleUserActive(Long id) {
        return null;
    }

    @Override
    public void deleteUser(Long id) {

    }

    @Override
    public Long getTotalUserCount() {
        return null;
    }

    @Override
    public Long getActiveUserCount() {
        return null;
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
}
