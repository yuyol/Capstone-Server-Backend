package com.yyws.capstone_server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yyws.capstone_server.dto.DeviceDto;
import com.yyws.capstone_server.entity.Device;
import com.yyws.capstone_server.entity.Model;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@SpringBootTest
class CapstoneServerApplicationTests {

	@Autowired
	private RedisTemplate<String,Object> redisTemplate;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void contextLoads() {
	}

	@Test
	void setDevice() {
		Device d1 = new Device(7935192, "LOLIN(WEMOS) D1 R2 & mini", 80, 50168, 4194304, 1, null, LocalDateTime.now());
		Device d2 = new Device(2, "test device", 80, 50168, 4194304, 1, null, null);
		Device d3 = new Device(4024575816L, "ESP32S3", 240, 50168, 8388608, 1, null, LocalDateTime.now());
		//Device d4 = new Device(7935192, "LOLIN(WEMOS) D1 R2 & mini", 80, 50168, 4194304, 1, null, LocalDateTime.now());
		redisTemplate.opsForValue().set("capstone:device:7935192", d1);
		redisTemplate.opsForValue().set("capstone:device:2", d2);

		Object rawD1 = redisTemplate.opsForValue().get("capstone:device:7935192");
		Object rawD2 = redisTemplate.opsForValue().get("capstone:device:4024575816L");
		//Object rawD3 = redisTemplate.opsForValue().get("capstone:device:7935192");
		Device getD1 = objectMapper.convertValue(rawD1, Device.class);

		LocalDateTime lastHeartBeat = getD1.getLastHeartBeat();
		System.out.println(lastHeartBeat);
	}

	@Test
	void getDevices() {
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
		System.out.println(devices);
	}

	@Test
	void setModel() {
		Model od_animal = new Model(1, "od_animal.ino.bin", 90.0);
		Model od_person = new Model(2, "od_person.ino.bin", 75.0);
		Model od_multi = new Model(3, "od_multi.ino.bin", 80.0);
		Model wifi_position = new Model(4, "wifi_position.ino.bin", 90.0);
		Model person_detection = new Model(5, "person_detection.ino.bin", 90.0);

		redisTemplate.opsForValue().set("capstone:model:1",od_animal);
		redisTemplate.opsForValue().set("capstone:model:2",od_person);
		redisTemplate.opsForValue().set("capstone:model:3",od_multi);
		redisTemplate.opsForValue().set("capstone:model:4",wifi_position);
		redisTemplate.opsForValue().set("capstone:model:5",person_detection);

		Object o = redisTemplate.opsForValue().get("capstone:model:1");
		Model model = objectMapper.convertValue(o, Model.class);
		System.out.println(model);
	}

	@Test
	void findAllModel() {
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
		System.out.println(models);
	}

}
