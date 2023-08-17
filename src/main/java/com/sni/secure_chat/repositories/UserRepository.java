package com.sni.secure_chat.repositories;

import com.sni.secure_chat.model.dto.UserDTO;
import com.sni.secure_chat.model.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<UserDTO> findByUserNameAndPassword(String userName, String password);

    @Query("SELECT u.publicKey from User u where u.userId = :userId")
    String getPublicKeyByUserId(Integer userId);

    @Query("SELECT u.privateKey from User u where u.userId = :userId")
    String getPrivateKeyByUserId(Integer userId);

    Optional<User> findByUserName(String userName);
}
