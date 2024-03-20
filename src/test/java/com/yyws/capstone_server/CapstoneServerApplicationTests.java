package com.yyws.capstone_server;

import com.yyws.capstone_server.dto.DeviceDto;
import com.yyws.capstone_server.entity.Device;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@SpringBootTest
class CapstoneServerApplicationTests {

	@Autowired
	private RedisTemplate<String,Object> redisTemplate;

	@Test
	void contextLoads() {
	}

	@Test
	void setDevice() {
		Device d1 = new Device(7935192, "LOLIN(WEMOS) D1 R2 & mini", 80, 50168, 4194304, 1, null, LocalDateTime.now());
		Device d2 = new Device(2, "test device", 80, 50168, 4194304, 1, null, null);
		redisTemplate.opsForValue().set("capstone:device:7935192", d1);
		redisTemplate.opsForValue().set("capstone:device:2", d2);

		Device getD1 = (Device) redisTemplate.opsForValue().get("capstone:device:7935192");
		LocalDateTime lastHeartBeat = getD1.getLastHeartBeat();
		System.out.println(lastHeartBeat);
	}

	@Test
	void getDevices() {
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
	}

}
