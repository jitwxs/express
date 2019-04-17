package com.example.express.security.validate.mobile;

import com.example.express.domain.enums.ResponseErrorCodeEnum;
import com.example.express.security.SecurityConstants;
import com.example.express.security.exception.DefaultAuthException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Objects;

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

        checkSmsCode(mobile);

        UserDetails userDetails = userDetailsService.loadUserByUsername(mobile);

        // 此时鉴权成功后，应当重新 new 一个拥有鉴权的 authenticationResult 返回
        SmsCodeAuthenticationToken authenticationResult = new SmsCodeAuthenticationToken(userDetails, userDetails.getAuthorities());

        authenticationResult.setDetails(authenticationToken.getDetails());

        return authenticationResult;
    }

    private void checkSmsCode(String mobile) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String inputCode = request.getParameter(SecurityConstants.LOGIN_MOBILE_CODE_PARAMETER);

        Map<String, String> smsCode = (Map<String, String>) request.getSession().getAttribute("smsCode");
        if(smsCode == null) {
            throw new DefaultAuthException(ResponseErrorCodeEnum.SMS_CODE_NOT_EXIST);
        }

        String applyMobile = smsCode.get("mobile");
        String code = smsCode.get("code");

        if(!applyMobile.equals(mobile)) {
            throw new DefaultAuthException(ResponseErrorCodeEnum.SMS_TEL_NOT_MATCH);
        }
        if(!Objects.equals(code, inputCode)) {
            throw new DefaultAuthException(ResponseErrorCodeEnum.VERIFY_CODE_ERROR);
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
