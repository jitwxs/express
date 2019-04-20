package com.example.express.config;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
public class RedisConfig {
    @Bean(name= "jedisPool")
    @Autowired
    public JedisPool jedisPool(@Qualifier("spring.redis.pool") JedisPoolConfig config,
                               @Value("${spring.redis.host}")String host,
                               @Value("${spring.redis.password}")String password,
                               @Value("${spring.redis.timeout}") int timeout,
                               @Value("${spring.redis.port}")int port) {
        if (StringUtils.isBlank(password)){
            return new JedisPool(config, host, port,timeout);
        }
        return new JedisPool(config, host, port,timeout,password);
    }

    @Bean(name= "spring.redis.pool")
    public JedisPoolConfig jedisPoolConfig (
            @Value("${spring.redis.jedis.pool.max-active}")int maxActivei,
            @Value("${spring.redis.jedis.pool.max-idle}")int maxIdle,
            @Value("${spring.redis.jedis.pool.max-wait}")int maxWaitMillis) {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(maxActivei);
        config.setMaxIdle(maxIdle);
        config.setMaxWaitMillis(maxWaitMillis);
        return config;
    }
}
