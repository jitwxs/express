package com.example.express.service.impl;

import com.example.express.BaseTests;
import com.example.express.common.constant.RedisKeyConstant;
import com.example.express.service.DataCompanyService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;

import static org.junit.Assert.*;

@Slf4j
public class DataCompanyServiceImplTest extends BaseTests {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private DataCompanyService dataCompanyService;

    @Test
    public void testCache() {
        String key = RedisKeyConstant.DATA_COMPANY;
        List list = redisTemplate.opsForList().range(key, 0, -1);
        if(list.size() == 0) {
            redisTemplate.opsForList().rightPushAll(key, dataCompanyService.listAll());
            list = redisTemplate.opsForList().range(key, 0, -1);
        }
        log.info("company size:{}, first:{},end:{}", list.size(), list.get(0), list.get(list.size()-1));
    }

}