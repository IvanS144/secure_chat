package com.sni.secure_chat.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HttpException extends RuntimeException{
    private int statusCode;
    private ErrorMessage errorMessage;

    public HttpException(int statusCode, String message){
        this.statusCode = statusCode;
        this.errorMessage = new ErrorMessage(message);
    }
}
