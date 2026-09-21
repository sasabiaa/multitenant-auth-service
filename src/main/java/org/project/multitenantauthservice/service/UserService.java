package org.project.multitenantauthservice.service;

import org.apache.coyote.BadRequestException;
import org.project.multitenantauthservice.entity.dto.request.AuthRequest;
import org.project.multitenantauthservice.entity.dto.request.UserRequest;
import org.project.multitenantauthservice.entity.dto.response.AuthResponse;
import org.project.multitenantauthservice.entity.dto.response.UserResponse;

public interface UserService {

    UserResponse createUser(UserRequest request) throws BadRequestException;

    AuthResponse login(AuthRequest request);
}
