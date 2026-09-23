package org.project.multitenantauthservice.service.implementation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.multitenantauthservice.entity.User;
import org.project.multitenantauthservice.entity.UserRole;
import org.project.multitenantauthservice.entity.dto.request.AuthRequest;
import org.project.multitenantauthservice.entity.dto.request.UserRequest;
import org.project.multitenantauthservice.entity.dto.response.AuthResponse;
import org.project.multitenantauthservice.entity.dto.response.UserResponse;
import org.project.multitenantauthservice.exception.BadRequestException;
import org.project.multitenantauthservice.repository.UserRepository;
import org.project.multitenantauthservice.service.JwtService;
import org.project.multitenantauthservice.service.UserService;
import org.project.multitenantauthservice.util.ValidationUtil;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;


@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final StringRedisTemplate stringRedisTemplate;

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

    @Override
    public AuthResponse login(AuthRequest request) {

        User user = userRepository.findByEmailOrUsername(
                        request.getEmailOrUsername(), request.getEmailOrUsername())
                .orElseThrow(() -> new BadRequestException("Wrong Email/Username or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadRequestException("Wrong Email/Username or password");
        }

        String token;

        try {
            token = stringRedisTemplate.opsForValue().get(user.getUsername());
        } catch (Exception e) {
            log.warn("Redis unavailable, falling back to generate new token", e);
            token = null;
        }

        if (token == null) {
            token = jwtService.generateToken(user);
            try {
                stringRedisTemplate.opsForValue().set(user.getUsername(), token, Duration.ofDays(7));
            } catch (Exception e) {
                log.warn("Failed to cache token in Redis", e);
            }
        }

        return AuthResponse.builder()
                .token(token)
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }
}
