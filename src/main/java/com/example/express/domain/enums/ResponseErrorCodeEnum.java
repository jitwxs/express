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
