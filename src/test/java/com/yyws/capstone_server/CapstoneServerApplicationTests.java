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
		redisTemplate.opsForValue().set("capstone:device:7935192", d1);
		redisTemplate.opsForValue().set("capstone:device:2", d2);

		Object rawD1 = redisTemplate.opsForValue().get("capstone:device:7935192");
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
		Model cnn = new Model(1, "CNN");
		Model rnn = new Model(2, "RNN");
		Model perceptron = new Model(3, "Percept");
		redisTemplate.opsForValue().set("capstone:model:1",cnn);
		redisTemplate.opsForValue().set("capstone:model:2",rnn);
		redisTemplate.opsForValue().set("capstone:model:3",perceptron);

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
