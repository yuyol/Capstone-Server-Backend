package com.yyws.capstone_server.service;

import com.yyws.capstone_server.dto.LoginDto;
import com.yyws.capstone_server.dto.UsersDto;

public interface UsersService {
    UsersDto login(LoginDto loginDto);

    void createUser(UsersDto usersDto);
}
