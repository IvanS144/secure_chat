package com.sni.secure_chat.exceptions;

public class ConflictException extends HttpException{
    public ConflictException(String message){
        super(409, message);
    }
}
