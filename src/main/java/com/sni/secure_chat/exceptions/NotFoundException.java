package com.sni.secure_chat.exceptions;

public class NotFoundException extends HttpException{
    public NotFoundException(String message){
        super(404, message);
    }
}
