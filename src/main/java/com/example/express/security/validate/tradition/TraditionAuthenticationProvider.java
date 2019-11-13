package com.example.express.security.validate.tradition;

import com.example.express.common.util.DateUtils;
import com.example.express.domain.bean.SysUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * @author xiangsheng.wu
 * @date 2019年05月01日 20:51
 */
@Slf4j
public class TraditionAuthenticationProvider implements AuthenticationProvider {
    private UserDetailsService userDetailsService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        TraditionAuthenticationToken authenticationToken = (TraditionAuthenticationToken) authentication;

        String inputName = (String) authenticationToken.getPrincipal();
        String inputPassword = (String) authenticationToken.getCredentials();

        // userDetails为数据库中查询到的用户信息
        UserDetails userDetails = userDetailsService.loadUserByUsername(inputName);

        // 如果是自定义AuthenticationProvider，需要手动密码校验
        if (!new BCryptPasswordEncoder().matches(inputPassword, userDetails.getPassword())) {
            throw new BadCredentialsException("密码错误");
        }

        // 校验账户状态
        authenticationChecks(userDetails);

        // 此时鉴权成功后，应当重新 new 一个拥有鉴权的 authenticationResult 返回
        TraditionAuthenticationToken authenticationResult = new TraditionAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        authenticationResult.setDetails(authenticationToken.getDetails());

        return authenticationResult;
    }



    @Override
    public boolean supports(Class<?> authentication) {
        // 这里不要忘记，和UsernamePasswordAuthenticationToken比较
        return authentication.equals(TraditionAuthenticationToken.class);
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
