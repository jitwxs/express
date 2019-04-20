package com.example.express.service;

import com.example.express.domain.enums.ResponseErrorCodeEnum;

/**
 * 短信服务
 * @date 2019年04月20日 14:59
 */
public interface SmsService {
    /**
     * 发送短信
     * @param tel 手机号码
     * @param code 验证码
     * @param min 有效时间
     */
    ResponseErrorCodeEnum send(String tel, String code, String min);

    /**
     * 校验短信
     * @author jitwxs
     * @since 2018/4/25 15:33
     */
    ResponseErrorCodeEnum check(String sessionTel, String sessionCode, String tel, String code);
}
