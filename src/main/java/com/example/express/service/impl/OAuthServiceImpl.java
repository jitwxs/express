package com.example.express.service.impl;

import com.example.express.service.OAuthService;
import com.example.express.service.RedisClient;
import com.example.express.util.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class OAuthServiceImpl implements OAuthService {
    @Autowired
    private RedisClient redisClient;

    @Value("${project.redis.key.oauth-state}")
    public String oauthStateKey;

    @Override
    public String genState() {
        String state = RandomUtils.time();
        // 保证生成的state未存在于redis中
        while(redisClient.sismember(oauthStateKey, state)) {
            state = RandomUtils.time();
        }

        // 保存state
        redisClient.sadd(oauthStateKey, state);

        return state;
    }

    @Override
    public boolean checkState(String state) {
        Boolean flag = redisClient.sismember(oauthStateKey, state);
        // 如果不存在，代表state非法；否则合法，并将其从缓存中删除
        if(flag == null || !flag) {
            return false;
        } else {
            redisClient.srem(oauthStateKey, state);
            return true;
        }
    }
}
