package com.sni.secure_chat.services.impl;

import com.sni.secure_chat.exceptions.NotFoundException;
import com.sni.secure_chat.model.dto.ChatUserDetails;
import com.sni.secure_chat.model.dto.UserDTO;
import com.sni.secure_chat.model.dto.requests.UserRequest;
import com.sni.secure_chat.model.entities.User;
import com.sni.secure_chat.repositories.UserRepository;
import com.sni.secure_chat.services.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;

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

    @Override
    public UserDetailsService userDetailsService() {
        return username -> {
            User u = userRepository.findByUserName(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
            return modelMapper.map(u, ChatUserDetails.class);
        };
    }

    @Override
    public UserDTO findUserByUserName(String userName) {
        User u = userRepository.findByUserName(userName).orElseThrow(() -> new NotFoundException("User not found"));
        return modelMapper.map(u, UserDTO.class);
    }

    @Override
    public List<UserDTO> getAll() {
        return userRepository.findAll().stream().map(u -> modelMapper.map(u, UserDTO.class)).toList();
    }

    @Override
    public UserDTO findById(int userId){
        User u = userRepository.findById(userId).orElseThrow(()-> new NotFoundException("User not found"));
        return modelMapper.map(u, UserDTO.class);
    }

    @Override
    public ChatUserDetails findUserDetailsByUserName(String userName){
        User u = userRepository.findByUserName(userName).orElseThrow(() -> new NotFoundException("User not found"));
        return modelMapper.map(u, ChatUserDetails.class);
    }
}
