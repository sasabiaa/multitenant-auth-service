package org.project.multitenantauthservice.service.implementation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.multitenantauthservice.entity.User;
import org.project.multitenantauthservice.entity.UserRole;
import org.project.multitenantauthservice.entity.dto.request.UserRequest;
import org.project.multitenantauthservice.entity.dto.response.UserResponse;
import org.project.multitenantauthservice.exception.BadRequestException;
import org.project.multitenantauthservice.repository.UserRepository;
import org.project.multitenantauthservice.service.UserService;
import org.project.multitenantauthservice.util.ValidationUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponse createUser(UserRequest request) {
        if (!ValidationUtil.isValidEmail(request.getUserEmail())
                || !ValidationUtil.isValidUsername(request.getUsername())
                || !ValidationUtil.isValidPassword(request.getPassword())) {
            log.error("Invalid format");
            throw new BadRequestException("Data input tidak valid");
        }

        Optional<User> existing = userRepository.findByEmailOrUsername(
                request.getUserEmail(), request.getUsername());

        if (existing.isPresent()) {
            User existingUser = existing.get();
            if (existingUser.getEmail().equalsIgnoreCase(request.getUserEmail())) {
                log.error("Email already used");
                throw new BadRequestException("Email sudah terdaftar");
            }
            log.error("Username already used");
            throw new BadRequestException("Username sudah digunakan");
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getUserEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(UserRole.USER_MEMBER)
                .build();

        userRepository.save(user);

        log.info("User successfully saved. Username: {}, Email: {}",
                request.getUsername(), request.getUserEmail());

        return UserResponse.builder()
                .userEmail(request.getUserEmail())
                .username(request.getUsername())
                .build();
    }

}
