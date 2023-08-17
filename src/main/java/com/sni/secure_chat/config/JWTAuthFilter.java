package com.sni.secure_chat.config;

import com.sni.secure_chat.exceptions.UnauthorizedException;
import com.sni.secure_chat.model.dto.ChatUserDetails;
import com.sni.secure_chat.model.dto.UserDTO;
import com.sni.secure_chat.services.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.stream.Stream;
import javax.servlet.http.Cookie;

@Component
public class JWTAuthFilter extends OncePerRequestFilter {
    @Value("${authorization.prefix}")
    private String prefix;
    @Value("${authorization.token.secret}")
    private String signingKey;
    private final ModelMapper modelMapper;
    private final UserService userService;

    public JWTAuthFilter(ModelMapper modelMapper, UserService userService) {
        this.modelMapper = modelMapper;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String cookieContent =request.getCookies()!=null ? Stream.of(request.getCookies()).filter(cookie->"auth-cookie".equals(cookie.getName())).findFirst().map(Cookie::getValue).orElse(null) : null;
        if (cookieContent == null) {
            System.out.println("if");
            filterChain.doFilter(request, response);
            return;
        }
        String token = cookieContent.replace(prefix, "");
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(signingKey)
                    .parseClaimsJws(token)
                    .getBody();
            UserDTO u = userService.findUserByUserName(claims.getSubject());
            ChatUserDetails uDetails = modelMapper.map(u, ChatUserDetails.class);
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(uDetails, null, uDetails.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (Exception e) {
            e.printStackTrace();
            //logger.error("JWT Authentication failed from: " + httpServletRequest.getRemoteHost());
            throw new UnauthorizedException("Sesija je istekla");

        }
        System.out.println("filter");
        filterChain.doFilter(request, response);
    }
}