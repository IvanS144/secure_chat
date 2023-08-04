package com.sni.secure_chat.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.SecureRandom;

@Configuration
public class SecurityConfig {
    @Bean
    public SecureRandom secureRandom(){
        return new SecureRandom();
    }
}
