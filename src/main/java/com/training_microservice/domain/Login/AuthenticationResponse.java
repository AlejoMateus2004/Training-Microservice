package com.training_microservice.domain.Login;

import lombok.Data;

@Data
public class AuthenticationResponse {
    private String username;
    private String role;
    private String jwt;

}
