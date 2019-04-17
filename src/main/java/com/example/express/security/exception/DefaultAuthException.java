package com.example.express.security.exception;

import com.example.express.domain.enums.ResponseErrorCodeEnum;
import org.springframework.security.core.AuthenticationException;

/**
 * 自定义鉴权异常
 */
public class DefaultAuthException extends AuthenticationException {
    public DefaultAuthException(String msg, Throwable t) {
        super(msg, t);
    }

    public DefaultAuthException(String msg) {
        super(msg);
    }

    public DefaultAuthException(ResponseErrorCodeEnum codeEnum) {
        super(codeEnum.getMsg());
    }
}
