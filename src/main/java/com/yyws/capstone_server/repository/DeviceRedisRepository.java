package com.yyws.capstone_server.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yyws.capstone_server.dto.DeviceDto;
import com.yyws.capstone_server.entity.DeployRecord;
import com.yyws.capstone_server.entity.Device;
import com.yyws.capstone_server.entity.UserDeviceRelation;
import com.yyws.capstone_server.exception.ResourceNotFoundException;
import com.yyws.capstone_server.mapper.ServerMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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

    public void registerDevice(UserDeviceRelation userDeviceRelation) {
        String key = "capstone:userDeviceRelation:" + userDeviceRelation.getEmail() + ":" + userDeviceRelation.getDeviceId();
        redisTemplate.opsForValue().set(key, userDeviceRelation);
    }



    public UserDeviceRelation findRelationship(UserDeviceRelation userDeviceRelation) {
        String key = "capstone:userDeviceRelation:" + userDeviceRelation.getEmail() + ":" + userDeviceRelation.getDeviceId();
        return objectMapper.convertValue(redisTemplate.opsForValue().get(key), UserDeviceRelation.class);

    }

    public List<UserDeviceRelation> searchOwnRelation(String email) {

        Set<String> keys = redisTemplate.keys("capstone:userDeviceRelation:"+email+":*");
        return keys.stream()
                .map(key -> objectMapper.convertValue(redisTemplate.opsForValue().get(key), UserDeviceRelation.class))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
    public List<UserDeviceRelation> findOwnersByDeviceId(String deviceId) {
        String pattern = "capstone:userDeviceRelation:*:" + deviceId;

        List<UserDeviceRelation> owners = new ArrayList<>();

        // Scan keys that match the pattern
        Set<String> keys = redisTemplate.keys(pattern);

        if (keys != null && !keys.isEmpty()) {
            // Retrieve all matching UserDeviceRelation objects
            for (String key : keys) {
                UserDeviceRelation relation = objectMapper.convertValue(redisTemplate.opsForValue().get(key),  UserDeviceRelation.class);
                if (relation != null) {
                    owners.add(relation);
                }
            }
        }

        return owners;
    }



    public Device searchDeviceById(String deviceId) {
        String key = "capstone:device:" + deviceId;

        return objectMapper.convertValue(redisTemplate.opsForValue().get(key), Device.class);
    }

    public void deleteDeviceFromUser(UserDeviceRelation deviceUserRelation) {

        // Find the key to delete
        String keyToDelete = "capstone:userDeviceRelation:"+deviceUserRelation.getEmail()+":"+deviceUserRelation.getDeviceId();

        // Delete the key from Redis
        redisTemplate.delete(keyToDelete);

    }


    public List<DeployRecord> searchDeployRecord(String email) {
        Set<String> keys = redisTemplate.keys("capstone:record:" + email + ":*");
        List<DeployRecord> collect = keys.stream()
                .map(key -> objectMapper.convertValue(redisTemplate.opsForValue().get(key), DeployRecord.class))
                .collect(Collectors.toList());
        return collect;
    }
}
