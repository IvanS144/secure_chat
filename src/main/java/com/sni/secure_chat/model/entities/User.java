package com.sni.secure_chat.model.entities;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name="user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="user_id")
    private Integer userId;
    @Basic
    @Column(name="user_name")
    private String userName;
    @Basic
    @Column(name="password")
    private String password;
    @Basic
    @Column(name="private_key")
    private String privateKey;
    @Basic
    @Column(name="public_key")
    private String publicKey;
    @Basic
    @Column(name="private_key_pem")
    private String privateKeyPem;
    @Basic
    @Column(name="public_key_pem")
    private String publicKeyPem;
}
