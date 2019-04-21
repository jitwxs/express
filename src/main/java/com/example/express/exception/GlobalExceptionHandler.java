package com.example.express.exception;

import com.example.express.common.constant.SecurityConstant;
import com.example.express.common.util.HttpClientUtils;
import com.example.express.domain.ResponseResult;
import com.example.express.domain.enums.ResponseErrorCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 异常处理
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
    /**
     * 参数校验
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ResponseResult handleMethodArgumentNotValidException(MethodArgumentNotValidException e){
        BindingResult bindingResult = e.getBindingResult();

        StringBuilder sb = new StringBuilder("校验失败:");

        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            sb.append(fieldError.getField() + "：" + fieldError.getDefaultMessage() + ", ");
        }

        log.error("参数错误，错误信息：{}", sb.toString());
        return ResponseResult.failure(ResponseErrorCodeEnum.PARAMETER_ERROR.getCode(), sb.toString());
    }

    @ExceptionHandler(value = CustomException.class)
    @ResponseBody
    public ResponseResult customException(CustomException e) {
        log.error("错误，错误栈：{}", HttpClientUtils.getStackTraceAsString(e));
        return ResponseResult.failure(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(value = AccessDeniedException.class)
    public void accessDeniedException(HttpServletResponse response) {
        try {
            response.sendRedirect(SecurityConstant.LOGIN_SUCCESS_URL);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}