package com.sni.secure_chat.services;

import com.sni.secure_chat.model.dto.UserDTO;
import com.sni.secure_chat.model.dto.requests.UserRequest;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService {
    UserDTO register(UserRequest userRequest);
    UserDetailsService userDetailsService();
    UserDTO findUserByUserName(String userName);
    List<UserDTO> getAll();
}
