package com.sni.secure_chat.model.dto.requests;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class UserRequest {
    @NotBlank
    private String userName;
    @NotBlank
    private String password;
}
