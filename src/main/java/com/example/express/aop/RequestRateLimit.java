package com.example.express.aop;


import com.example.express.domain.enums.RateLimitEnum;
import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestRateLimit {
    RateLimitEnum limit() default RateLimitEnum.RRLimit_5_1;
}
