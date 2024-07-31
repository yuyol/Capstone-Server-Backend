package com.yyws.capstone_server.mapper;

import com.yyws.capstone_server.dto.UsersDto;
import com.yyws.capstone_server.entity.Users;

public class UsersMapper {

    public static Users UsersDtoToUsers(UsersDto usersDto, Users users) {
        users.setUserId(usersDto.getUserId());
        users.setUniqueId(usersDto.getUniqueId());
        users.setUsername(usersDto.getUsername());
        users.setEmail(usersDto.getEmail());
        users.setMobileNumber(usersDto.getMobileNumber());
        users.setPassword(usersDto.getPassword());
        return users;
    }

    public static UsersDto UsersToUsersDto(Users users, UsersDto usersDto) {
        usersDto.setUserId(users.getUserId());
        usersDto.setUniqueId(users.getUniqueId());
        usersDto.setUsername(users.getUsername());
        usersDto.setEmail(users.getEmail());
        usersDto.setMobileNumber(users.getMobileNumber());
        usersDto.setPassword(users.getPassword());
        return usersDto;
    }
}
