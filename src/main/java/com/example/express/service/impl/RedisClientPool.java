package com.example.express.service.impl;

import com.example.express.service.RedisClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.List;
import java.util.Set;

@Service
public class RedisClientPool implements RedisClient {
    @Autowired
    private JedisPool jedisPool;

    @Override
    public String set(String key, String value) {
        Jedis jedis = jedisPool.getResource();
        String result = jedis.set(key, value);
        jedis.close();
        return result;
    }

    @Override
    public String get(String key) {
        Jedis jedis = jedisPool.getResource();
        String result = jedis.get(key);
        jedis.close();
        return result;
    }

    @Override
    public Boolean exists(String key) {
        Jedis jedis = jedisPool.getResource();
        Boolean result = jedis.exists(key);
        jedis.close();
        return result;
    }

    @Override
    public Long expire(String key, int seconds) {
        Jedis jedis = jedisPool.getResource();
        Long result = jedis.expire(key, seconds);
        jedis.close();
        return result;
    }

    @Override
    public Long ttl(String key) {
        Jedis jedis = jedisPool.getResource();
        Long result = jedis.ttl(key);
        jedis.close();
        return result;
    }

    @Override
    public Long incr(String key) {
        Jedis jedis = jedisPool.getResource();
        Long result = jedis.incr(key);
        jedis.close();
        return result;
    }

    @Override
    public Long hset(String key, String field, String value) {
        Jedis jedis = jedisPool.getResource();
        Long result = jedis.hset(key, field, value);
        jedis.close();
        return result;
    }

    @Override
    public String hget(String key, String field) {
        Jedis jedis = jedisPool.getResource();
        String result = jedis.hget(key, field);
        jedis.close();
        return result;
    }

    @Override
    public Long hdel(String key, String... field) {
        Jedis jedis = jedisPool.getResource();
        Long result = jedis.hdel(key, field);
        jedis.close();
        return result;
    }

    @Override
    public Boolean hexists(String key, String field) {
        Jedis jedis = jedisPool.getResource();
        Boolean result = jedis.hexists(key, field);
        jedis.close();
        return result;
    }

    @Override
    public Set<String> hkeys(String key) {
        Jedis jedis = jedisPool.getResource();
        Set<String> result = jedis.hkeys(key);
        jedis.close();
        return result;
    }

    @Override
    public List<String> hvals(String key) {
        Jedis jedis = jedisPool.getResource();
        List<String> result = jedis.hvals(key);
        jedis.close();
        return result;
    }

    @Override
    public Long sadd(String key, String... members) {
        Jedis jedis = jedisPool.getResource();
        Long result = jedis.sadd(key, members);
        jedis.close();
        return result;
    }

    @Override
    public Set<String> smembers(String key) {
        Jedis jedis = jedisPool.getResource();
        Set<String> result = jedis.smembers(key);
        jedis.close();
        return result;
    }

    @Override
    public Boolean sismember(String key, String member) {
        Jedis jedis = jedisPool.getResource();
        Boolean result = jedis.sismember(key, member);
        jedis.close();
        return result;
    }

    @Override
    public Long srem(String key, String... members) {
        Jedis jedis = jedisPool.getResource();
        Long result = jedis.srem(key, members);
        jedis.close();
        return result;
    }

    @Override
    public Long scard(String key) {
        Jedis jedis = jedisPool.getResource();
        Long result = jedis.scard(key);
        jedis.close();
        return result;
    }

    @Override
    public String srandmember(String key) {
        Jedis jedis = jedisPool.getResource();
        String result = jedis.srandmember(key);
        jedis.close();
        return result;
    }

    @Override
    public Long zadd(String key, Double score, String member) {
        Jedis jedis = jedisPool.getResource();
        Long result = jedis.zadd(key, score, member);
        jedis.close();
        return result;
    }

    @Override
    public Set<String> zrange(String key, long start, long end) {
        Jedis jedis = jedisPool.getResource();
        Set<String> result = jedis.zrange(key, start, end);
        jedis.close();
        return result;
    }

    @Override
    public Set<String> zrangeByScore(String key, double min, double max) {
        Jedis jedis = jedisPool.getResource();
        Set<String> result = jedis.zrangeByScore(key, min, max);
        jedis.close();
        return result;
    }

    @Override
    public Long zrem(String key, String... members) {
        Jedis jedis = jedisPool.getResource();
        Long result = jedis.zrem(key, members);
        jedis.close();
        return result;
    }

    @Override
    public Long zremrangeByRank(String key, long start, long end) {
        Jedis jedis = jedisPool.getResource();
        Long result = jedis.zremrangeByRank(key, start, end);
        jedis.close();
        return result;
    }

    @Override
    public Long zremrangeByScore(String key, double min, double max) {
        Jedis jedis = jedisPool.getResource();
        Long result = jedis.zremrangeByScore(key, min, max);
        jedis.close();
        return result;
    }

    @Override
    public Long del(String key) {
        Jedis jedis = jedisPool.getResource();
        Long result = jedis.del(key);
        jedis.close();
        return result;
    }
}
