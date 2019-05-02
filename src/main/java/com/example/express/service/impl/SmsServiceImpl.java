package com.example.express.service.impl;

import com.example.express.common.constant.SessionKeyConstant;
import com.example.express.common.util.StringUtils;
import com.example.express.domain.enums.ResponseErrorCodeEnum;
import com.example.express.service.SmsService;
import com.github.qcloudsms.SmsSingleSender;
import com.github.qcloudsms.SmsSingleSenderResult;
import lombok.extern.slf4j.Slf4j;
import com.example.express.common.util.HttpClientUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
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
    @Value("${project.sms.valid-min}")
    private String smsValidMins;

    @Value("${project.sms.app-id}")
    public void setAppId(Integer appId) {
        APP_ID = appId;
    }
    @Value("${project.sms.app-key}")
    public void setAppKey(String appKey) {
        APP_KEY = appKey;
    }
    @Value("${project.sms.template-id}")
    public void setTemplateId(Integer templateId) {
        TEMPLATE_ID = templateId;
    }
    @Value("${project.sms.sign}")
    public void setSmsSign(String smsSign) {
        SMS_SIGN = smsSign;
    }

    @Override
    public ResponseErrorCodeEnum send(HttpSession session, String tel, String code) {
        // 校验手机号码合法
        if(!StringUtils.isValidTel(tel)) {
            return ResponseErrorCodeEnum.TEL_INVALID;
        }

        SmsSingleSender sender = new SmsSingleSender(APP_ID,APP_KEY);
        ArrayList<String> params = new ArrayList<>();
        // 添加模板参数
        params.add(code);
        params.add(smsValidMins);

        try {
            SmsSingleSenderResult result = sender.sendWithParam("86", tel, TEMPLATE_ID, params, SMS_SIGN, "", "");
            if(result.result == 0) {
                // 设置 session
                setSmsSession(session, tel, code);
                return ResponseErrorCodeEnum.SUCCESS;
            } else {
                // TODO 处理服务端错误码
                log.error("验证码发送失败，手机号：{}，错误信息：{}", tel, result.errMsg);
                return ResponseErrorCodeEnum.SEND_SMS_ERROR;
            }
        } catch (Exception e) {
            log.error("验证码发送出现异常：{}", HttpClientUtils.getStackTraceAsString(e));
            return ResponseErrorCodeEnum.SEND_SMS_ERROR;
        }
    }

    @Override
    public ResponseErrorCodeEnum check(HttpSession session, String tel, String code) {
        // 校验手机号码合法
        if(!StringUtils.isValidTel(tel)) {
            return ResponseErrorCodeEnum.TEL_INVALID;
        }

        String sessionTel = (String)session.getAttribute(SessionKeyConstant.SMS_TEL);
        String sessionCode = (String)session.getAttribute(SessionKeyConstant.SMS_CODE);
        String sessionTimestamp = (String)session.getAttribute(SessionKeyConstant.SMS_TIMESTAMP);

        // 是否存在
        if(StringUtils.isAnyBlank(sessionTel, sessionCode, sessionTimestamp)) {
            cleanSmsSession(session);
            return ResponseErrorCodeEnum.SMS_CODE_NOT_EXIST;
        }

        // 手机号是否一致
        if(!sessionTel.equals(tel)) {
            return ResponseErrorCodeEnum.SMS_TEL_NOT_MATCH;
        }

        // 是否过期
        long nowTimestamp = System.currentTimeMillis() / 1000;
        if((nowTimestamp - Long.parseLong(sessionTimestamp) > Integer.parseInt(smsValidMins) * 60)) {
            cleanSmsSession(session);
            return ResponseErrorCodeEnum.SMS_EXPIRE;
        }

        // 验证码是否匹配
        if(!sessionCode.equals(code)) {
            return ResponseErrorCodeEnum.VERIFY_CODE_ERROR;
        }

        // 登陆成功
        cleanSmsSession(session);
        return ResponseErrorCodeEnum.SUCCESS;
    }

    private void cleanSmsSession(HttpSession session) {
        session.removeAttribute(SessionKeyConstant.SMS_TEL);
        session.removeAttribute(SessionKeyConstant.SMS_CODE);
        session.removeAttribute(SessionKeyConstant.SMS_TIMESTAMP);
    }

    private void setSmsSession(HttpSession session, String mobile, String verifyCode) {
        session.setAttribute(SessionKeyConstant.SMS_TEL, mobile);
        session.setAttribute(SessionKeyConstant.SMS_CODE, verifyCode);
        session.setAttribute(SessionKeyConstant.SMS_TIMESTAMP, String.valueOf(System.currentTimeMillis()));
    }
}
