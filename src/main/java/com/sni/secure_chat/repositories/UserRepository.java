package com.sni.secure_chat.repositories;

import com.sni.secure_chat.model.dto.UserDTO;
import com.sni.secure_chat.model.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<UserDTO> findByUserNameAndPassword(String userName, String password);
}
