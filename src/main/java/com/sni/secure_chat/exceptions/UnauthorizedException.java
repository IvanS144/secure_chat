package com.sni.secure_chat.exceptions;

public class UnauthorizedException extends HttpException{
    public UnauthorizedException(String message){
        super(401, message);
    }
}
