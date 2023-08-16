package com.sni.secure_chat.services;

import com.sni.secure_chat.model.dto.UserDTO;
import com.sni.secure_chat.model.dto.requests.UserRequest;

public interface LoginService {
    UserDTO login(String userName, String password);
    UserDTO register(UserRequest userRequest);
}
