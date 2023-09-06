package com.sni.secure_chat.controllers;

import com.sni.secure_chat.model.dto.UserDTO;
import com.sni.secure_chat.model.entities.User;
import com.sni.secure_chat.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class UsersController {
    private final UserService userService;

    public UsersController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers(){
        List<UserDTO> list = userService.getAll();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getById(@PathVariable Integer id){
        UserDTO u = userService.findById(id);
        return ResponseEntity.ok(u);
    }

    @GetMapping("/me")
    public ResponseEntity<Void> checkStatus(){
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
