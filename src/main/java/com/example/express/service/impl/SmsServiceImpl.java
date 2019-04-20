package com.example.express.service.impl;

import com.example.express.domain.enums.ResponseErrorCodeEnum;
import com.example.express.service.SmsService;
import com.example.express.util.HttpClientUtils;
import com.example.express.util.StringUtils;
import com.github.qcloudsms.SmsSingleSender;
import com.github.qcloudsms.SmsSingleSenderResult;
import com.github.qcloudsms.httpclient.HTTPException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;

/**
 * 短信服务
 * @date 2019年04月20日 14:59
 */
@Slf4j
@Service
public class SmsServiceImpl implements SmsService {
    private static Integer APP_ID;
    private static String APP_KEY;
    private static Integer TEMPLATE_ID;
    private static String SMS_SIGN;

    @Value("${tencent.sms.app-id}")
    public void setAppId(Integer app_id) {
        APP_ID = app_id;
    }

    @Value("${tencent.sms.app-key}")
    public void setAppKey(String appKey) {
        APP_KEY = appKey;
    }

    @Value("${tencent.sms.tempate-id}")
    public void setTemplateId(Integer templateId) {
        TEMPLATE_ID = templateId;
    }

    @Value("${tencent.sms.sign}")
    public void setSmsSign(String smsSign) {
        SMS_SIGN = smsSign;
    }

    public ResponseErrorCodeEnum send(String tel, String code, String min) {
        SmsSingleSender sender = new SmsSingleSender(APP_ID,APP_KEY);
        ArrayList<String> params = new ArrayList<>();
        // 添加模板参数
        params.add(code);
        params.add(min);

        try {
            SmsSingleSenderResult result = sender.sendWithParam("86", tel, TEMPLATE_ID, params, SMS_SIGN, "", "");
            if(result.result == 0) {
                return ResponseErrorCodeEnum.SUCCESS;
            } else {
                log.error("验证码发送失败");
                return ResponseErrorCodeEnum.SEND_SMS_ERROR;
            }
        } catch (Exception e) {
            log.error("验证码发送出现异常：{}", HttpClientUtils.getStackTraceAsString(e));
            return ResponseErrorCodeEnum.SEND_SMS_ERROR;
        }
    }

    public ResponseErrorCodeEnum check(String sessionTel, String sessionCode, String tel, String code) {
        if (StringUtils.isBlank(sessionTel) || StringUtils.isBlank(sessionCode)) {
            return ResponseErrorCodeEnum.SMS_EXPIRE;
        }

        //验证手机号码是否一致
        if (!sessionTel.equals(tel)) {
            return ResponseErrorCodeEnum.SMS_TEL_NOT_MATCH;
        }

        //校验验证码是否正确
        if (!sessionCode.equals(code)) {
            return ResponseErrorCodeEnum.VERIFY_CODE_ERROR;
        }

        // 验证码登陆无需验证用户是否存在
        return ResponseErrorCodeEnum.SUCCESS;
    }
}
