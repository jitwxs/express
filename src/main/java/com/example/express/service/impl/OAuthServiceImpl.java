package com.example.express.service.impl;

import com.example.express.common.constant.RedisKeyConstant;
import com.example.express.common.util.RandomUtils;
import com.example.express.service.OAuthService;
import com.example.express.service.RedisClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OAuthServiceImpl implements OAuthService {
    @Autowired
    private RedisClient redisClient;

    @Override
    public String genState() {
        String state = RandomUtils.time();
        // 保证生成的state未存在于redis中
        while(redisClient.sismember(RedisKeyConstant.OAUTH_STATE, state)) {
            state = RandomUtils.time();
        }

        // 保存state
        redisClient.sadd(RedisKeyConstant.OAUTH_STATE, state);

        return state;
    }

    @Override
    public boolean checkState(String state) {
        Boolean flag = redisClient.sismember(RedisKeyConstant.OAUTH_STATE, state);
        // 如果不存在，代表state非法；否则合法，并将其从缓存中删除
        if(flag == null || !flag) {
            return false;
        } else {
            redisClient.srem(RedisKeyConstant.OAUTH_STATE, state);
            return true;
        }
    }
}
