package com.training_microservice.domain.Login;

import javax.validation.constraints.NotEmpty;

import lombok.Data;


@Data
public class AuthenticationRequest {
    @NotEmpty
    private String username;
    @NotEmpty
    private String password;
}