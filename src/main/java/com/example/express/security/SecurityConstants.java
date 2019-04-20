package com.example.express.security;

/**
 * Security 相关常量
 * @author jitwxs
 * @since 2019/1/8 23:50
 */
public class SecurityConstants {
    /**
     * 无权限或登录失败，被引导跳转的 Url
     */
    public static final String UN_AUTHENTICATION_URL = "/auth/index";
    /**
     * 退出登录的 Url
     */
    public static final String LOGOUT_URL = "/logout";
    /**
     * 登陆成功后，被引导跳转的 Url
     */
    public static final String LOGIN_SUCCESS_URL = "/";
    /**
     * Session 过期被引导跳转的 Url
     */
    public static final String INVALID_SESSION_URL = "/auth/session/invalid";

    /**
     * 用户名密码登录请求处理url
     */
    public static final String LOGIN_PROCESSING_URL_FORM = "/auth/form-login";
    /**
     * 手机验证码登录请求处理url
     */
    public static final String LOGIN_PROCESSING_URL_MOBILE = "/auth/mobile-login";

    /**
     * 手机验证码登录手机号表单字段名
     */
    public static final String LOGIN_MOBILE_PARAMETER = "mobile";
    /**
     * 手机验证码登录验证码表单字段名
     */
    public static final String LOGIN_MOBILE_CODE_PARAMETER = "smsCode";
    /**
     * 验证码登陆表单字段名
     */
    public static final String VALIDATE_CODE_PARAMETER = "verifyCode";

    /**
     * 三方登陆相关 Url 前缀
     */
     public static final String THIRD_LOGIN_URL_PREFIX = "/auth/third-login";
     /**
      * QQ 登陆
      */
     public static final String QQ_LOGIN_URL = THIRD_LOGIN_URL_PREFIX + "/qq";
     /**
      * QQ 登陆回调 URL
      */
     public static final String QQ_CALLBACK_URL = THIRD_LOGIN_URL_PREFIX + "/qqCallback";
    /**
     * 验证码相关 Url 前缀
     * 包括图形验证码图片、短信验证码接口等等...
     */
    public static final String VALIDATE_CODE_URL_PREFIX = "/auth/code";
    /**
     * 图形验证码 Url
     */
    public static final String VALIDATE_CODE_PIC_URL = VALIDATE_CODE_URL_PREFIX + "/getVerifyCode";
    /**
     * 验证码错误 Url
     */
    public static final String VALIDATE_CODE_ERR_URL = VALIDATE_CODE_URL_PREFIX + "/error";
}
