package com.sni.secure_chat.config;

import com.sni.secure_chat.services.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

import java.security.SecureRandom;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final UserService userService;
    private final JWTAuthFilter filterJWT;

    public SecurityConfig(UserService userService, JWTAuthFilter filterJWT) {
        this.userService = userService;
        this.filterJWT = filterJWT;
    }

    @Bean
    public SecureRandom secureRandom(){
        return new SecureRandom();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userService.userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.headers().contentSecurityPolicy("default-src 'self';" +
                "script-src 'self';" +
                "style-src 'self' https://fonts.googleapis.com https://cdn.jsdelivr.net 'unsafe-inline';" +
                "font-src https://fonts.gstatic.com https://localhost:8443/bootstrap-icons.02685dabe0850e40.woff2 https://localhost:8443/bootstrap-icons.8463cb1e163733b5.woff");


        http.csrf()
//                .disable()
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers("/", "index.html", "/css/**", "/js/**", "/assets/*", "/favicon.ico").permitAll()
                .antMatchers("/swagger-ui/**","/v2/api-docs/**","/swagger-resources/**").permitAll()
                .antMatchers(HttpMethod.POST, "/register", "/auth/login", "/auth/logout").permitAll()
                .antMatchers("/messages", "/users","/messages/**", "/users/**")
                .authenticated()
                .and()
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(filterJWT, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
