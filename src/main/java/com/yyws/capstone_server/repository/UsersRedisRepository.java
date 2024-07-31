package com.yyws.capstone_server.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yyws.capstone_server.dto.UsersDto;
import com.yyws.capstone_server.entity.Users;
import com.yyws.capstone_server.exception.ResourceNotFoundException;
import com.yyws.capstone_server.mapper.UsersMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class UsersRedisRepository {

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    public void save(Users user) {
        String key = "capstone:users:"+user.getEmail();
        redisTemplate.opsForValue().set(key, user);
    }

    public List<Users> findUserByEmailAsList(String email) {

        Set<String> keys = redisTemplate.keys("capstone:users:*");

        return keys.stream()
                .map(key -> objectMapper.convertValue(redisTemplate.opsForValue().get(key), Users.class))
                .filter(user -> user != null && user.getEmail().equals(email))
                .collect(Collectors.toList());
    }


    public UsersDto findUserByEmail(String email) {
        List<Users> userByEmailAsList = findUserByEmailAsList(email);
        if(userByEmailAsList.size() == 0) {
            throw new ResourceNotFoundException("User", "Email", email);
        }
        UsersDto user = UsersMapper.UsersToUsersDto(userByEmailAsList.get(0), new UsersDto());
        return user;
    }
}
