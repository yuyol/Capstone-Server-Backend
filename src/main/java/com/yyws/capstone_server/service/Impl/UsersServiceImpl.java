package com.yyws.capstone_server.service.Impl;

import com.yyws.capstone_server.dto.LoginDto;
import com.yyws.capstone_server.dto.UsersDto;
import com.yyws.capstone_server.entity.Users;
import com.yyws.capstone_server.exception.WrongPasswordException;
import com.yyws.capstone_server.mapper.UsersMapper;
import com.yyws.capstone_server.repository.UsersRedisRepository;
import com.yyws.capstone_server.service.UsersService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@AllArgsConstructor
public class UsersServiceImpl implements UsersService {
    @Autowired
    UsersRedisRepository usersRedisRepository;

    @Override
    public UsersDto login(LoginDto loginDto) {

        UsersDto user = usersRedisRepository.findUserByEmail(loginDto.getEmail());

        if (!loginDto.getPassword().equals(user.getPassword())) {
            throw new WrongPasswordException();
        }

        return user;
    }

    @Override
    public void createUser(UsersDto usersDto) {
        List<Users> byEmail = usersRedisRepository.findUserByEmailAsList(usersDto.getEmail());
        if (byEmail.size() > 0) return;

        Users user = UsersMapper.UsersDtoToUsers(usersDto, new Users());

        usersRedisRepository.save(user);
    }
}
