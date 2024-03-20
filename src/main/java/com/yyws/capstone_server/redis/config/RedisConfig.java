package com.yyws.capstone_server.redis.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.yyws.capstone_server.dto.DeviceDto;
import com.yyws.capstone_server.entity.Device;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;

@Configuration
public class RedisConfig {
//    @Bean
//    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
//        RedisTemplate<String, Object> template = new RedisTemplate<>();
//        // 设置连接工厂
//        template.setConnectionFactory(redisConnectionFactory);
//        // 创建JSON序列化工具
////        GenericJackson2JsonRedisSerializer jsonRedisSerializer = new GenericJackson2JsonRedisSerializer();
//
//        // Customize ObjectMapper to register Java 8 Date and Time module
//        ObjectMapper objectMapper = new ObjectMapper()
//                .registerModule(new JavaTimeModule()) // Register Java Time Module
//                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // Optional: to serialize dates as ISO strings
//
//        // Use the customized ObjectMapper in the serializer
//        GenericJackson2JsonRedisSerializer jsonRedisSerializer = new GenericJackson2JsonRedisSerializer(objectMapper);
//
//        // 设置key的序列化
//        template.setKeySerializer(RedisSerializer.string());
//        template.setHashKeySerializer(RedisSerializer.string());
//        // 设置value的序列化
//        template.setValueSerializer(jsonRedisSerializer);
//        template.setHashValueSerializer(jsonRedisSerializer);
//        return template;
//    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);

        // Serializer for String type keys
        RedisSerializer<String> stringSerializer = RedisSerializer.string();

        // Serializer for DeviceDto values
        Jackson2JsonRedisSerializer<Device> deviceDtoSerializer = new Jackson2JsonRedisSerializer<>(Device.class);

        // Optional: Customize the underlying ObjectMapper if needed
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        deviceDtoSerializer.setObjectMapper(objectMapper);

        template.setKeySerializer(stringSerializer);
        template.setValueSerializer(deviceDtoSerializer);
        template.setHashKeySerializer(stringSerializer);
        template.setHashValueSerializer(deviceDtoSerializer);

        return template;
    }
}
