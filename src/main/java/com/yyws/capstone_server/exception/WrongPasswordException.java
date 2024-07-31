package com.yyws.capstone_server.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class WrongPasswordException extends RuntimeException{
    public WrongPasswordException() {
        super(String.format("Wrong password or user doesn't exist"));
    }
}
