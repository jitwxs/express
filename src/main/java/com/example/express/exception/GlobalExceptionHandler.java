package com.example.express.exception;

import com.example.express.common.constant.SecurityConstant;
import com.example.express.common.util.HttpClientUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 异常处理
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = CustomException.class)
    public ModelAndView customException(CustomException e) {
        log.error("[异常错误] code：{}, msg: {}, 错误：{}", e.getCode(), e.getMessage(), HttpClientUtils.getStackTraceAsString(e));

        Map<String, Object> map = new HashMap<>(16);
        map.put("errorCode", e.getCode());
        map.put("errorMsg", e.getMessage());

        return new ModelAndView("error/error", map);
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