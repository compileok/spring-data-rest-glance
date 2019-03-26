package com.github.springdatarestglance;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;

/**
 *
 * @author xudeming
 * @date 2019/3/20
 */
public class LettuceTest {

	public static void main(String[] args) {
		RedisURI uri = new RedisURI();
		uri.setHost("redis-cache2.228");
		uri.setPort(5001);
		uri.setDatabase(0);
		RedisClient redisClient = RedisClient.create(uri);
		StatefulRedisConnection<String, String> connection = redisClient.connect();
		RedisCommands<String, String> syncCommands = connection.sync();

		syncCommands.set("testKey", "Hello, Redis!");
		System.out.println(syncCommands.get("testKey"));

		connection.close();
		redisClient.shutdown();
	}

}
