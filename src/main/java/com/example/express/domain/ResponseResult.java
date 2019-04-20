package com.example.express.domain;

import com.example.express.domain.enums.ResponseErrorCodeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
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
        return failure(errorCode.getCode(), errorCode.getMsg());
    }

    public static ResponseResult failure(int code, String msg) {
        return new ResponseResult(code, msg, null);
    }

    public static ResponseResult failure(ResponseErrorCodeEnum errorCode, Object data) {
        return new ResponseResult(errorCode.getCode(), errorCode.getMsg(), data);
    }

    public static ResponseResult failure(ResponseErrorCodeEnum errorCode, Object[] params) {
        return failure(errorCode, params, null);
    }

    public static ResponseResult failure(ResponseErrorCodeEnum errorCode, Object[] params, Object data) {
        String msg = String.format(errorCode.getMsg(), params);
        return new ResponseResult(errorCode.getCode(), msg, data);
    }
}
