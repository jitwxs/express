package com.example.express.service.impl;

import com.example.express.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * @author xiangsheng.wu
 * @date 2019年04月30日 16:29
 */
@Slf4j
@Service
public class RedisServiceImpl implements RedisService {
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public boolean checkRequestRateLimit(final String key, final int expireTime, final int max, TimeUnit timeUnit, String userAgent) {
        long count = redisTemplate.opsForValue().increment(key, 1);
        long time = redisTemplate.getExpire(key);
        /*
         * count = 1: 表示在本次请求前，key不存在或者key已过期。
         * time = -1: 表示未设置过期时间
         */
        if (count == 1 || time == -1) {
            redisTemplate.expire(key, expireTime, timeUnit);
        }

        if (count <= max) {
            return false;
        }

        log.info("Express api request limit rate:too many requests: key={}, redis count={}, max count={}, " +
                "expire time= {} s, user-agent={} ", key, count, max, expireTime, userAgent);
        return true;
    }
}
