package org.example;

import redis.clients.jedis.Jedis;

public class RedisWriter {
    public static void save(String value) {

        try (Jedis jedis = new Jedis("localhost", 6379)) {

            jedis.rpush("numbers", value);
            System.out.println("Saved :" + value);

        }

    }
}
