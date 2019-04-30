package com.example.express.service;

import java.util.concurrent.TimeUnit;

/**
 * @author xiangsheng.wu
 * @date 2019年04月30日 16:29
 */
public interface RedisService {
    boolean checkRequestRateLimit(final String key, final int expireTime, final int max, TimeUnit timeUnit, String userAgent);
}
