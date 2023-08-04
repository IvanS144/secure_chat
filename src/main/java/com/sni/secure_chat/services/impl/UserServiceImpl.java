package com.sni.secure_chat.services.impl;

import com.sni.secure_chat.model.dto.UserDTO;
import com.sni.secure_chat.model.dto.requests.UserRequest;
import com.sni.secure_chat.model.entities.User;
import com.sni.secure_chat.repositories.UserRepository;
import com.sni.secure_chat.services.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    @PersistenceContext
    private EntityManager entityManager;

    public UserServiceImpl(UserRepository userRepository, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public UserDTO register(UserRequest userRequest) {
        User u = modelMapper.map(userRequest, User.class);
        userRepository.saveAndFlush(u);
        entityManager.refresh(u);
        return modelMapper.map(u, UserDTO.class);
    }
}
