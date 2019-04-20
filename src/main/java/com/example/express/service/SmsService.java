package com.example.express.service;

import com.example.express.domain.enums.ResponseErrorCodeEnum;

import javax.servlet.http.HttpSession;

/**
 * 短信服务
 * @date 2019年04月20日 14:59
 */
public interface SmsService {
    /**
     * 发送短信
     * @param tel 手机号码
     * @param code 验证码
     */
    ResponseErrorCodeEnum send(HttpSession session, String tel, String code);

    /**
     * 校验短信
     */
    ResponseErrorCodeEnum check(HttpSession session, String tel, String code);
}
