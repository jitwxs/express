package com.example.express.domain.enums;

import lombok.Getter;

/**
 * 错误码美剧
 * @date 2019/4/16 23:51
 */
@Getter
public enum ResponseErrorCodeEnum {
    SUCCESS(0, "成功"),
    PARAMETER_ERROR(1000, "参数错误"),
    ROLE_ERROR(1001, "角色错误"),
    USERNAME_EXIST_ERROR(1002, "用户名已经被使用"),
    TEL_NOT_LEGAL(1003, "手机号码不合法"),
    REGISTRY_ERROR(1004, "注册失败"),
    THIRD_LOGIN_ERROR(1005, "三方登陆失败"),
    USER_NOT_EXIST(1006, "用户不存在"),

    SMS_SEND_INTERVAL_TOO_SHORT(1988, "短信发送间隔不足%s分钟"),
    SMS_EXPIRE(1989, "短信验证码失效，请重新发送"),
    SEND_SMS_ERROR(1990, "发送短信失败"),
    PASSWORD_ERROR(1991, "密码输入错误，请检查用户名和密码"),
    TEL_NOT_EXIST(1992, "该手机号码尚未注册，请先注册再登录"),
    SMS_TEL_NOT_MATCH(1993, "申请的手机号码与登录手机号码不匹配"),
    SMS_CODE_NOT_EXIST(1994, "请先获取短信验证码"),
    VERIFY_CODE_ERROR(1995, "验证码输入错误"),
    LOGIN_ERROR(1996, "登陆失败"),
    SESSION_EXPIRE(1997, "Session已过期，请重新登录"),
    OPERATION_ERROR(1998, "操作失败"),
    SYSTEM_ERROR(1999, "系统错误")
    ;

    private int code;

    private String msg;

    ResponseErrorCodeEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
