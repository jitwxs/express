package com.example.express.security.validate.tradition;

import com.example.express.common.constant.SecurityConstant;
import com.example.express.security.exception.DefaultAuthException;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 传统登录的鉴权过滤器
 * @author jitwxs
 * @since 2019/1/9 13:52
 */
public class TraditionAuthenticationFilter extends AbstractAuthenticationProcessingFilter {
    /**
     * 是否仅 POST 方式
     */
    private boolean postOnly = true;

    private String SPRING_SECURITY_FORM_USERNAME_KEY = "username";

    private String SPRING_SECURITY_FORM_PASSWORD_KEY = "password";

    public TraditionAuthenticationFilter() {
        // 短信登录的请求
        super(new AntPathRequestMatcher(SecurityConstant.LOGIN_PROCESSING_URL_FORM, "POST"));
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        if (postOnly && !"POST".equals(request.getMethod())) {
            throw new AuthenticationServiceException(
                    "Authentication method not supported: " + request.getMethod());
        }

        String verifyCode = request.getParameter(SecurityConstant.VALIDATE_CODE_PARAMETER);

        if (!validateVerify(verifyCode)) {
            throw new DefaultAuthException("验证码输入错误");
        }

        String username = request.getParameter(SPRING_SECURITY_FORM_USERNAME_KEY);
        String password = request.getParameter(SPRING_SECURITY_FORM_PASSWORD_KEY);

        TraditionAuthenticationToken authRequest = new TraditionAuthenticationToken(username, password);

        // Allow subclasses to set the "details" property
        setDetails(request, authRequest);

        return this.getAuthenticationManager().authenticate(authRequest);
    }

    private boolean validateVerify(String inputVerify) {
        //获取当前线程绑定的request对象
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        // 不分区大小写
        // 这个validateCode是在servlet中存入session的名字
        String validateCode = ((String) request.getSession().getAttribute("validateCode")).toLowerCase();
        inputVerify = inputVerify.toLowerCase();

//        log.info("验证码：{}, 用户输入：{}", validateCode, inputVerify);
        return validateCode.equals(inputVerify);
    }

    protected void setDetails(HttpServletRequest request, TraditionAuthenticationToken authRequest) {
        authRequest.setDetails(authenticationDetailsSource.buildDetails(request));
    }

    public void setPostOnly(boolean postOnly) {
        this.postOnly = postOnly;
    }
}
