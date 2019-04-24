package com.example.express;

import org.assertj.core.util.Lists;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author xiangsheng.wu
 * @date 2019年04月24日 17:07
 */
public class RedisTest extends BaseTests {
    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void testString() {
        String key = genValue();
        redisTemplate.opsForValue().set(key, "testValue");

        String s = (String) redisTemplate.opsForValue().get(key);
        Assert.assertEquals("testValue", s);
    }

    @Test
    public void testSet() {
        String key = genValue();
        String testValue = genValue();
        redisTemplate.opsForSet().add(key, testValue, genValue(), genValue());

        Boolean isExist = redisTemplate.opsForSet().isMember(key, testValue);
        Assert.assertEquals(true, isExist);
    }

    @Test
    public void testList() {
        String key = genValue();
        ArrayList<Integer> list = Lists.list(1, 2, 3, 4, 5, 6);
        redisTemplate.opsForList().rightPushAll(key, list);

        List result = redisTemplate.opsForList().range(key, 0, -1);

        Assert.assertEquals(list, result);
    }

    private String genValue() {
        Random random = new Random();
        return random.nextInt(100) + "";
    }
}
