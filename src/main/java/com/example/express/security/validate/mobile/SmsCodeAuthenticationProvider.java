package com.example.express.security.validate.mobile;

import com.example.express.common.constant.SecurityConstant;
import com.example.express.common.util.DateUtils;
import com.example.express.domain.bean.SysUser;
import com.example.express.domain.enums.ResponseErrorCodeEnum;
import com.example.express.security.exception.DefaultAuthException;
import com.example.express.service.SmsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * 短信登陆鉴权 Provider，要求实现 AuthenticationProvider 接口
 * @author jitwxs
 * @since 2019/1/9 13:59
 */
@Slf4j
@Component
public class SmsCodeAuthenticationProvider implements AuthenticationProvider {
    private UserDetailsService userDetailsService;
    @Autowired
    private SmsService smsService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        SmsCodeAuthenticationToken authenticationToken = (SmsCodeAuthenticationToken) authentication;

        String mobile = (String) authenticationToken.getPrincipal();

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String code = request.getParameter(SecurityConstant.LOGIN_MOBILE_CODE_PARAMETER);

        // 校验短信
        ResponseErrorCodeEnum codeEnum = smsService.check(request.getSession(), mobile, code);
        if(codeEnum != ResponseErrorCodeEnum.SUCCESS) {
            throw new DefaultAuthException(codeEnum);
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(mobile);

        // 校验账户状态
        authenticationChecks(userDetails);

        // 此时鉴权成功后，应当重新 new 一个拥有鉴权的 authenticationResult 返回
        SmsCodeAuthenticationToken authenticationResult = new SmsCodeAuthenticationToken(userDetails, userDetails.getAuthorities());

        authenticationResult.setDetails(authenticationToken.getDetails());

        return authenticationResult;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        // 判断 authentication 是不是 SmsCodeAuthenticationToken 的子类或子接口
        return SmsCodeAuthenticationToken.class.isAssignableFrom(authentication);
    }

    public void setUserDetailsService(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    private void authenticationChecks(UserDetails user) {
        if (!user.isAccountNonLocked()) {
            log.debug("User account is locked");

            throw new LockedException("账户已冻结，解冻时间为：" + DateUtils.format(((SysUser)user).getLockDate(), 1));
        }

        if (!user.isEnabled()) {
            log.debug("User account is disabled");

            throw new DisabledException("账户已失效，请联系管理员");
        }

        if (!user.isAccountNonExpired()) {
            log.debug("User account is expired");

            throw new AccountExpiredException("账户已过期，请联系管理员");
        }

        if (!user.isCredentialsNonExpired()) {
            log.debug("User credentials is expired");
            throw new CredentialsExpiredException("密码已过期");
        }
    }
}
