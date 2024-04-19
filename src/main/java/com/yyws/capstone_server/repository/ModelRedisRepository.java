package com.yyws.capstone_server.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yyws.capstone_server.entity.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class ModelRedisRepository {
    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    public List<Model> findAll() {
        // Find all keys matching the user pattern
        Set<String> modelKeys = redisTemplate.keys("capstone:model:*");

        List<Model> models = new ArrayList<>();

        // Fetch each user by key
        List<Object> objects = modelKeys.stream()
                .map(key -> redisTemplate.opsForValue().get(key))
                .collect(Collectors.toList());

        for (Object o: objects) {
            models.add(objectMapper.convertValue(o, Model.class));
        }

        return models;
    }
}
