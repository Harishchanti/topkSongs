package org.example;

import redis.clients.jedis.Jedis;

public class RedisWriter {
    /*public static void save(String value) {

        try (Jedis jedis = new Jedis("localhost", 6379)) {

            jedis.rpush("numbers", value);
            System.out.println("Saved :" + value);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
*/
    private static final Jedis jedis =

            new Jedis("localhost", 6379);

    public static void save(int number, long count) {

        try {

            jedis.zadd(
                    "topk",
                    count,
                    String.valueOf(number));
        } catch (Exception e) {
            System.out.println(
                    "Error while adding the entries in redis" + e.getMessage());
        }

    }

}
