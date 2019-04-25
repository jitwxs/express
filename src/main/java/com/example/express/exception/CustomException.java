package com.example.express.exception;

import com.example.express.domain.ResponseResult;
import com.example.express.domain.enums.ResponseErrorCodeEnum;
import lombok.Data;

/**
 * 默认异常处理
 * @date 2019年04月20日 13:42
 */
@Data
public class CustomException extends RuntimeException {
    private Integer code;

    public CustomException(ResponseErrorCodeEnum errorCodeEnum) {
        super(errorCodeEnum.getMsg());
        this.code = errorCodeEnum.getCode();
    }

    public CustomException(ResponseResult result) {
        super(result.getMsg());
        this.code = result.getCode();
    }

    public CustomException(Integer code , String info) {
        super(info);
        this.code = code;
    }
}
