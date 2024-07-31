package com.yyws.capstone_server.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yyws.capstone_server.entity.Users;
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
    public List<Users> findUserByEmail(String email) {

        Set<String> keys = redisTemplate.keys("capstone:users:*");

        return keys.stream()
                .map(key -> objectMapper.convertValue(redisTemplate.opsForValue().get(key), Users.class))
                .filter(user -> user != null && user.getEmail().equals(email))
                .collect(Collectors.toList());
    }

    public void save(Users user) {
        String key = "capstone:users:"+user.getEmail();
        redisTemplate.opsForValue().set(key, user);
    }
}
