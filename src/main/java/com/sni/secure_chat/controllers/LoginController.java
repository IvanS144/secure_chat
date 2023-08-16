package com.sni.secure_chat.controllers;

import com.sni.secure_chat.model.dto.UserDTO;
import com.sni.secure_chat.model.dto.requests.LoginRequest;
import com.sni.secure_chat.model.dto.requests.UserRequest;
import com.sni.secure_chat.model.entities.User;
import com.sni.secure_chat.services.LoginService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class LoginController {
    private final LoginService loginService;

    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @PostMapping("/login")
    public ResponseEntity<UserDTO> loin(@RequestBody @Valid LoginRequest loginRequest){
        UserDTO u = loginService.login(loginRequest.getUserName(), loginRequest.getPassword());
        return ResponseEntity.ok(u);
    }

    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(@RequestBody @Valid UserRequest userRequest){
        UserDTO u = loginService.register(userRequest);
        return ResponseEntity.ok(u);
    }
}
