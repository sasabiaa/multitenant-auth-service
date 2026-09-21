package org.project.multitenantauthservice.entity.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponse {

    private String username;

    private String userEmail;
}
