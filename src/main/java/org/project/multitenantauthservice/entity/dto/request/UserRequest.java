package org.project.multitenantauthservice.entity.dto.request;

import lombok.Data;

@Data
public class UserRequest {

    private String username;

    private String userEmail;

    private String password;
}
