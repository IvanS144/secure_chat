package com.sni.secure_chat.exceptions;

public class ForbiddenException extends HttpException{
    public ForbiddenException(String message){
        super(403, message);
    }
}
