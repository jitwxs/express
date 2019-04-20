package com.example.express.security.authentication;

import com.example.express.common.constant.SecurityConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 默认认证失败处理
 * @author jitwxs
 * @since 2019/1/8 23:29
 */
@Slf4j
@Component
public class DefaultAuthenticationFailureHandler implements AuthenticationFailureHandler {
    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        request.getSession().setAttribute("SPRING_SECURITY_LAST_EXCEPTION", exception);
        this.redirectStrategy.sendRedirect(request, response, SecurityConstant.UN_AUTHENTICATION_URL);
    }
}
