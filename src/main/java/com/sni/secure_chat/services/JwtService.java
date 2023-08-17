package com.sni.secure_chat.services;

import com.sni.secure_chat.model.dto.UserDTO;
import org.springframework.security.core.userdetails.UserDetails;

public interface JwtService {
    String extractUserName(String token);
    String generateToken(UserDetails userDetails);
    boolean isTokenValid(String token, UserDetails userDetails);
    String generateToken(UserDTO userDTO);
}
