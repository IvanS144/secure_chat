package com.sni.secure_chat.services.impl;

import com.fasterxml.jackson.databind.ser.Serializers;
import com.sni.secure_chat.exceptions.NotFoundException;
import com.sni.secure_chat.model.dto.UserDTO;
import com.sni.secure_chat.model.dto.requests.UserRequest;
import com.sni.secure_chat.model.entities.User;
import com.sni.secure_chat.repositories.UserRepository;
import com.sni.secure_chat.services.LoginService;
import com.sni.secure_chat.util.CryptoUtil;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Service
@Transactional
public class LoginServiceImpl implements LoginService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    @PersistenceContext
    private EntityManager entityManager;

    public LoginServiceImpl(UserRepository userRepository, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public UserDTO login(String userName, String password) {
        return userRepository.findByUserNameAndPassword(userName, password).orElseThrow(() -> new NotFoundException("Wrong credentials"));
    }

    @Override
    public UserDTO register(UserRequest userRequest) {
        try {
            User u = new User();
            u.setUserName(userRequest.getUserName());
            u.setPassword(userRequest.getPassword());
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            u.setPrivateKeyPem(CryptoUtil.keyToPem(keyPair.getPrivate()));
            u.setPublicKeyPem(CryptoUtil.keyToPem(keyPair.getPublic()));
            u.setPublicKey(CryptoUtil.keyToBase64(keyPair.getPublic()));
            u.setPrivateKey(CryptoUtil.keyToBase64(keyPair.getPrivate()));
            userRepository.saveAndFlush(u);
            entityManager.refresh(u);
            return modelMapper.map(u, UserDTO.class);
        }
        catch(Exception e){
            throw new RuntimeException(e);
        }
    }
}
