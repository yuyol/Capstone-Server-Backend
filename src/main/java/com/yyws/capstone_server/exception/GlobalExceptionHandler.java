package com.yyws.capstone_server.exception;

import com.yyws.capstone_server.dto.ErrorResponseDto;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponseDto> ConstraintViolationExceptionHandler(ConstraintViolationException exception,
                                                                                WebRequest webRequest) {
        ErrorResponseDto errorResponseDto = new ErrorResponseDto(
                webRequest.getDescription(false),
                HttpStatus.INTERNAL_SERVER_ERROR,
                new ArrayList<>(exception.getConstraintViolations()).get(0).getMessage(),
                LocalDateTime.now()
        );

        return new ResponseEntity<>(errorResponseDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }

//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    public ResponseEntity<ErrorResponseDto> MethodArgumentNotValidExceptionHandler(MethodArgumentNotValidException exception,
//                                                                                   WebRequest webRequest) {
//        List<String> errorMessages = exception.getBindingResult()
//                .getFieldErrors()
//                .stream()
//                .map(FieldError::getDefaultMessage)
//                .collect(Collectors.toList());
//
//        String combinedErrorMessages = String.join(", ", errorMessages);
//
//        ErrorResponseDto errorResponseDto = new ErrorResponseDto(
//                webRequest.getDescription(false),
//                HttpStatus.BAD_REQUEST,
//                combinedErrorMessages,
//                LocalDateTime.now()
//        );
//
//        return new ResponseEntity<>(errorResponseDto, HttpStatus.BAD_REQUEST);
//    }
}
