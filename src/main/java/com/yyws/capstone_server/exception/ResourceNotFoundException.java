package com.yyws.capstone_server.exception;

public class ResourceNotFoundException extends  RuntimeException{

    public ResourceNotFoundException(String resourceName, String fieldName, String fieldValue) {
        super(String.format("%s : '%s' can not find related %s",fieldName,fieldValue,resourceName));
    }
}
