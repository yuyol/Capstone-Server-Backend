package com.yyws.capstone_server.repository;

import com.yyws.capstone_server.dto.DeviceDto;
import com.yyws.capstone_server.entity.Device;
import com.yyws.capstone_server.mapper.ServerMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class DeviceRedisRepository {
    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    public List<Device> findAll() {
        // Find all keys matching the user pattern
        Set<String> deviceKeys = redisTemplate.keys("capstone:device:*");

        // Fetch each user by key
        List<Device> devices = deviceKeys.stream()
                .map(key -> (Device) redisTemplate.opsForValue().get(key))
                .collect(Collectors.toList());

        for (Device device :
                devices) {
            System.out.println(device);
        }

        return devices;
    }

    public DeviceDto findDeviceInfo(Long id) {
        StringBuilder sb = new StringBuilder("capstone:device:");
        sb.append(id);
        Device device = (Device) redisTemplate.opsForValue().get(sb.toString());
        return ServerMapper.DeviceToDeviceDto(device, new DeviceDto());
    }
}
