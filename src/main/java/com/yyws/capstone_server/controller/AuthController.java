package com.yyws.capstone_server.controller;

import com.yyws.capstone_server.constants.UsersConstants;
import com.yyws.capstone_server.dto.LoginDto;
import com.yyws.capstone_server.dto.ResponseDto;
import com.yyws.capstone_server.dto.UsersDto;
import com.yyws.capstone_server.service.UsersService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
public class AuthController {

    private final UsersService usersService;

    public AuthController(UsersService usersService) {
        this.usersService = usersService;
    }


    @PostMapping("/login")
    public ResponseEntity<UsersDto> login(@RequestBody LoginDto loginDto) {

        UsersDto usersDto = usersService.login(loginDto);
//        UsersDto usersDto = new UsersDto();
        System.out.println("test");

        return ResponseEntity.status(HttpStatus.OK)
                .body(usersDto);
    }

    @PostMapping("/register")
    public ResponseEntity<ResponseDto> register(@Valid @RequestBody UsersDto usersDto) {

        usersService.createUser(usersDto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseDto(UsersConstants.STATUS_201,UsersConstants.MESSAGE_201));
    }
}
