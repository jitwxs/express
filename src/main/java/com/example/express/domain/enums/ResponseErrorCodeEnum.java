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
    USERNAME_EXIST_ERROR(1002, "用户名已被注册，请更换用户名"),
    TEL_INVALID(1003, "手机号码不合法"),
    REGISTRY_ERROR(1004, "注册失败"),
    THIRD_LOGIN_ERROR(1005, "三方登陆失败"),
    USER_NOT_EXIST(1006, "用户不存在"),
    EXIST_UNFINISHED_ORDER(1007, "当前存在未完成的订单"),
    ORDER_CREATE_ERROR(1008, "订单创建失败"),
    ORDER_PAYMENT_CREATE_ERROR(1008, "订单支付信息创建失败"),
    FEEDBACK_TYPE_ERROR(1009, "反馈类型错误"),
    FEEDBACK_NOT_EMPTY(1010, "反馈内容不能为空"),
    FEEDBACk_LENGTH_OVER_255(1011, "反馈内容过长，请控制在255字符内"),
    ORDER_NOT_EXIST(1012, "订单不存在"),

    OPERATION_NOT_SUPPORT(1976, "操作不支持"),
    IDCARD_OR_REALNAME_EXIST(1977, "您已绑定实名信息"),
    STUDENT_IDCARD_NOT_NUMBER(1978, "学号必须为纯数字"),
    SCHOOL_NOT_EXIST(1979, "高校不存在"),
    TEL_EXIST(1980, "该手机号已被注册，请更换其他手机号"),
    USERNAME_DISABLE_MODIFY(1981, "用户名不支持修改"),
    PASSWORD_RESET_ERROR(1982, "密码重置失败"),
    PASSWORD_IS_EMPTY(1983, "密码不能为空"),
    REGISTER_ERROR(1984, "注册失败"),
    REAL_NAME_INVALID(1985, "姓名中含有非法字符"),
    SCHOOL_INVALID(1986, "高校不合法"),
    ID_CARD_INVALID(1987, "身份证号不合法"),
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
