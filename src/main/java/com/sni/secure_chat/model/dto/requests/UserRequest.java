package com.sni.secure_chat.model.dto.requests;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class UserRequest {
    @NotBlank
    private String userName;
    @NotBlank
    @Size(min = 16)
    private String password;
}
