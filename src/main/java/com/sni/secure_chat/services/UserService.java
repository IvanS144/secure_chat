package com.sni.secure_chat.services;

import com.sni.secure_chat.model.dto.UserDTO;
import com.sni.secure_chat.model.dto.requests.UserRequest;

public interface UserService {
    UserDTO register(UserRequest userRequest);
}
