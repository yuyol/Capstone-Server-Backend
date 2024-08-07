package com.yyws.capstone_server.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yyws.capstone_server.dto.DeviceDto;
import com.yyws.capstone_server.entity.Device;
import com.yyws.capstone_server.entity.UserDeviceRelation;
import com.yyws.capstone_server.mapper.ServerMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class DeviceRedisRepository {
    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    public List<Device> findAll() {
        // Find all keys matching the user pattern
        Set<String> deviceKeys = redisTemplate.keys("capstone:device:*");

        // Fetch each user by key
        List<Object> objects = deviceKeys.stream()
                .map(key -> redisTemplate.opsForValue().get(key))
                .collect(Collectors.toList());

        List<Device> devices = new ArrayList<>();

        for (Object o :
                objects) {
            devices.add(objectMapper.convertValue(o, Device.class));
        }

        return devices;
    }

    public DeviceDto findDeviceInfo(Long id) {
        StringBuilder sb = new StringBuilder("capstone:device:");
        sb.append(id);
        Object raw = redisTemplate.opsForValue().get(sb.toString());
        Device device = objectMapper.convertValue(raw, Device.class);
        return ServerMapper.DeviceToDeviceDto(device, new DeviceDto());
    }

    public void saveDevice(DeviceDto savedDevice) {
        String key = "capstone:device:"+savedDevice.getId();
        redisTemplate.opsForValue().set(key, savedDevice);
    }

    public void saveDeviceAsServer(DeviceDto savedDevice) {
        String key = "capstone:device:asServer:" + savedDevice.getId();
        redisTemplate.opsForValue().set(key, savedDevice);
    }

    public void registerDevice(String uniqueId, UserDeviceRelation userDeviceRelation) {
        String key = "capstone:userDeviceRelation:" + uniqueId;
        redisTemplate.opsForValue().set(key, userDeviceRelation);
    }

    public UserDeviceRelation findRelationByUniqueId(String uniqueId) {
        String key = "capstone:userDeviceRelation:" + uniqueId;
        return objectMapper.convertValue(redisTemplate.opsForValue().get(key), UserDeviceRelation.class);
    }

    public List<UserDeviceRelation> findRelationship(UserDeviceRelation userDeviceRelation) {
//        String key = "capstone:userDeviceRelation:*";
        Set<String> keys = redisTemplate.keys("capstone:userDeviceRelation:*");

        return keys.stream()
                .map(key -> objectMapper.convertValue(redisTemplate.opsForValue().get(key), UserDeviceRelation.class))
                .filter(relation -> relation != null && relation.getDeviceId().equals(userDeviceRelation.getDeviceId()) && relation.getEmail().equals(userDeviceRelation.getEmail()))
                .collect(Collectors.toList());
    }

    public List<UserDeviceRelation> searchOwnRelation(String email) {

        Set<String> keys = redisTemplate.keys("capstone:userDeviceRelation:*");
        return keys.stream()
                .map(key -> objectMapper.convertValue(redisTemplate.opsForValue().get(key), UserDeviceRelation.class))
                .filter(relation -> relation != null && relation.getEmail().equals(email))
                .collect(Collectors.toList());
    }


    public Device searchDeviceById(String deviceId) {
        String key = "capstone:device:" + deviceId;

        return objectMapper.convertValue(redisTemplate.opsForValue().get(key), Device.class);
    }
}
