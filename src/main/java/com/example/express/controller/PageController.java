package com.example.express.controller;

import com.example.express.common.constant.SecurityConstant;
import com.example.express.domain.ResponseResult;
import com.example.express.domain.bean.SysUser;
import com.example.express.domain.enums.ResponseErrorCodeEnum;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Controller
public class PageController {
    /**
     * 登陆成功页
     */
    @RequestMapping(SecurityConstant.LOGIN_SUCCESS_URL)
    public void showSuccessPage(@AuthenticationPrincipal SysUser sysUser, HttpServletResponse response) throws IOException {
        switch (sysUser.getRole()) {
            case DIS_FORMAL:
                response.sendRedirect("/completeInfo");
                return;
            case USER:
                response.sendRedirect("/user/dashboard");
                return;
            case ADMIN:
                response.sendRedirect("/admin/dashboard");
                return;
            case COURIER:
                response.sendRedirect("/courier/dashboard");
                return;
            default:
                response.sendRedirect("/index");
        }
    }

    /**
     * 处理验证码错误
     */
    @RequestMapping(SecurityConstant.VALIDATE_CODE_ERR_URL)
    public String codeError(ModelMap map) {
        map.put("message", ResponseErrorCodeEnum.VERIFY_CODE_ERROR.getMsg());
        return "login";
    }

    /**
     * 跳转到登录页
     */
    @RequestMapping(SecurityConstant.UN_AUTHENTICATION_URL)
    public String showAuthenticationPage(HttpSession session, HttpServletResponse response, ModelMap map) {
        Object exception = session.getAttribute(SecurityConstant.LAST_EXCEPTION);
        if(exception != null) {
            if(exception instanceof BadCredentialsException) {
                map.put("message", ResponseErrorCodeEnum.PASSWORD_ERROR.getMsg());
            } else if (exception instanceof AuthenticationException ){
                map.put("message", ((AuthenticationException)exception).getMessage());
            } else if (exception instanceof ResponseResult) {
                map.put("message", ((ResponseResult) exception).getMsg());
            } else if (exception instanceof String) {
                map.put("message", exception);
            }
        }

        session.removeAttribute(SecurityConstant.LAST_EXCEPTION);
        return "login";
    }

    /**
     * 跳转到信息补全页面
     * @author jitwxs
     * @date 2019/4/21 22:14
     */
    @RequestMapping("/completeInfo")
    public String showCompleteInfoPage() {
        return "completeInfo";
    }

    @GetMapping("/register")
    public String showRegister() { return "register"; }
}