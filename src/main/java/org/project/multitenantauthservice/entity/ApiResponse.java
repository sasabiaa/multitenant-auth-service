package org.project.multitenantauthservice.entity;

import lombok.*;

import java.time.Instant;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {
    private String status;
    private String message;
    private T data;
    private Instant timestamp;
}
