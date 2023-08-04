package com.sni.secure_chat.services.impl;

import com.sni.secure_chat.exceptions.NotFoundException;
import com.sni.secure_chat.model.dto.UserDTO;
import com.sni.secure_chat.repositories.UserRepository;
import com.sni.secure_chat.services.LoginService;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class LoginServiceImpl implements LoginService {
    private final UserRepository userRepository;

    public LoginServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDTO login(String userName, String password) {
        return userRepository.findByUserNameAndPassword(userName, password).orElseThrow(() -> new NotFoundException("Wrong credentials"));
    }
}
