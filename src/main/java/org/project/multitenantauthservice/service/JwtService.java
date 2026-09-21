package org.project.multitenantauthservice.service;

import org.project.multitenantauthservice.entity.User;

public interface JwtService {

    String generateToken(User user);
    String extractUsername(String token);
    boolean isTokenValid(String token, String username);
}
