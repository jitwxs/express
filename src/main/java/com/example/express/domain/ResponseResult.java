package com.example.express.domain;

import com.example.express.domain.enums.ResponseErrorCodeEnum;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class ResponseResult {
    private int code;
    private String msg;
    private Object data;

    public static ResponseResult success() {
        return success(null);
    }

    public static ResponseResult success(Object data) {
        return new ResponseResult(ResponseErrorCodeEnum.SUCCESS.getCode(), ResponseErrorCodeEnum.SUCCESS.getMsg(), data);
    }

    public static ResponseResult failure(ResponseErrorCodeEnum errorCode) {
        return new ResponseResult(errorCode.getCode(), errorCode.getMsg(), null);
    }

    public static ResponseResult failure(int code, String msg) {
        return new ResponseResult(code, msg, null);
    }

    public static ResponseResult failure(ResponseErrorCodeEnum errorCode, Object data) {
        return new ResponseResult(errorCode.getCode(), errorCode.getMsg(), data);
    }
}
