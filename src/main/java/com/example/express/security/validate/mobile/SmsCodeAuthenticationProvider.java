package com.example.express.security.validate.mobile;

import com.example.express.common.constant.SecurityConstant;
import com.example.express.common.constant.SessionKeyConstant;
import com.example.express.common.util.StringUtils;
import com.example.express.domain.enums.ResponseErrorCodeEnum;
import com.example.express.security.exception.DefaultAuthException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Arrays;

/**
 * 短信登陆鉴权 Provider，要求实现 AuthenticationProvider 接口
 * @author jitwxs
 * @since 2019/1/9 13:59
 */
public class SmsCodeAuthenticationProvider implements AuthenticationProvider {
    private UserDetailsService userDetailsService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        SmsCodeAuthenticationToken authenticationToken = (SmsCodeAuthenticationToken) authentication;

        String mobile = (String) authenticationToken.getPrincipal();

        ResponseErrorCodeEnum codeEnum = checkSmsCode(mobile);
        if(codeEnum != ResponseErrorCodeEnum.SUCCESS) {
            throw new DefaultAuthException(codeEnum);
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(mobile);

        // 此时鉴权成功后，应当重新 new 一个拥有鉴权的 authenticationResult 返回
        SmsCodeAuthenticationToken authenticationResult = new SmsCodeAuthenticationToken(userDetails, userDetails.getAuthorities());

        authenticationResult.setDetails(authenticationToken.getDetails());

        return authenticationResult;
    }

    private ResponseErrorCodeEnum checkSmsCode(String inputMobile) {
        // 短信有效期：900s
        long smsValidSeconds = 900;

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String inputCode = request.getParameter(SecurityConstant.LOGIN_MOBILE_CODE_PARAMETER);

        HttpSession session = request.getSession();

        String sessionTel = (String)session.getAttribute(SessionKeyConstant.SMS_TEL);
        String sessionCode = (String)session.getAttribute(SessionKeyConstant.SMS_CODE);
        String sessionTimestamp = (String)session.getAttribute(SessionKeyConstant.SMS_TIMESTAMP);

        // 是否存在
        if(StringUtils.isBlank(sessionTel) || StringUtils.isBlank(sessionCode) || StringUtils.isBlank(sessionTimestamp)) {
            cleanSmsSession(session, sessionTel, sessionCode, sessionTimestamp);
            return ResponseErrorCodeEnum.SMS_CODE_NOT_EXIST;
        }

        // 手机号是否一致
        if(!sessionTel.equals(inputMobile)) {
            return ResponseErrorCodeEnum.SMS_TEL_NOT_MATCH;
        }

        // 是否过期
        long nowTimestamp = System.currentTimeMillis() / 1000;
        if((nowTimestamp - Long.parseLong(sessionTimestamp) > smsValidSeconds)) {
            cleanSmsSession(session, sessionTel, sessionCode, sessionTimestamp);
            return ResponseErrorCodeEnum.SMS_EXPIRE;
        }

        // 验证码是否匹配
        if(!sessionCode.equals(inputCode)) {
            return ResponseErrorCodeEnum.VERIFY_CODE_ERROR;
        }

        // 登陆成功
        cleanSmsSession(session, sessionTel, sessionCode, sessionTimestamp);
        return ResponseErrorCodeEnum.SUCCESS;
    }

    private void cleanSmsSession(HttpSession session, String... str) {
        if(str.length > 0) {
            Arrays.stream(str).forEach(session::removeAttribute);
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        // 判断 authentication 是不是 SmsCodeAuthenticationToken 的子类或子接口
        return SmsCodeAuthenticationToken.class.isAssignableFrom(authentication);
    }

    public UserDetailsService getUserDetailsService() {
        return userDetailsService;
    }

    public void setUserDetailsService(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }
}
