package org.prkguides.blog.service;

import org.prkguides.blog.dto.JwtResponseDto;
import org.prkguides.blog.dto.LoginRequestDto;

public interface AuthService {

    JwtResponseDto authenticateUser(LoginRequestDto loginRequest);

    boolean validateToken(String token);
}
