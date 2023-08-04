package com.sni.secure_chat.services;

import com.sni.secure_chat.model.dto.UserDTO;

public interface LoginService {
    UserDTO login(String userName, String password);
}
