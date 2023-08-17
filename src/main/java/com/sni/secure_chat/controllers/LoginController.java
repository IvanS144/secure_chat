package com.sni.secure_chat.controllers;

import com.sni.secure_chat.model.dto.UserDTO;
import com.sni.secure_chat.model.dto.requests.LoginRequest;
import com.sni.secure_chat.model.dto.requests.UserRequest;
import com.sni.secure_chat.services.JwtService;
import com.sni.secure_chat.services.LoginService;
import com.sni.secure_chat.services.UserService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@CrossOrigin("http://localhost:4200")
public class LoginController {
    private final LoginService loginService;
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public LoginController(LoginService loginService, UserService userService, AuthenticationManager authenticationManager, JwtService jwtService) {
        this.loginService = loginService;
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<UserDTO> login(@RequestBody @Valid LoginRequest loginRequest){
        UserDTO u = loginService.login(loginRequest.getUserName(), loginRequest.getPassword());
        return ResponseEntity.ok(u);
    }

    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(@RequestBody @Valid UserRequest userRequest){
        UserDTO u = loginService.register(userRequest);
        return ResponseEntity.ok(u);
    }

    @PostMapping("/auth/login")
    public ResponseEntity<UserDTO> auth(@RequestBody @Valid LoginRequest loginRequest){
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUserName(), loginRequest.getPassword())
        );
        UserDTO u = userService.findUserByUserName(loginRequest.getUserName());
        String JWT = jwtService.generateToken(u);
        ResponseCookie cookie = ResponseCookie.from("auth-cookie", JWT).secure(false).httpOnly(true).maxAge(3600).build();
        return ResponseEntity.status(HttpStatus.OK).header(HttpHeaders.SET_COOKIE, cookie.toString()).body(u);
    }
}
