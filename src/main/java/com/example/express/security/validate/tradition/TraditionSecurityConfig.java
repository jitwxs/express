package com.example.express.security.validate.tradition;

import com.example.express.common.constant.SecurityConstant;
import com.example.express.security.handler.DefaultAuthenticationFailureHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.security.web.authentication.rememberme.RememberMeAuthenticationFilter;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

/**
 * 验证码登陆相关安全设置
 * @author jitwxs
 * @since 2019/1/8 23:58
 */
@Component
public class TraditionSecurityConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {
    @Autowired
    private TraditionUserDetailsService userDetailsService;
    @Autowired
    private DefaultAuthenticationFailureHandler defaultAuthenticationFailureHandler;
    @Autowired
    private DataSource dataSource;
    @Autowired
    private AuthenticationManager authenticationManager;

    @Bean
    public RememberMeServices rememberMeServices() {
        JdbcTokenRepositoryImpl rememberMeTokenRepository = new JdbcTokenRepositoryImpl();
        rememberMeTokenRepository.setDataSource(dataSource);

        PersistentTokenBasedRememberMeServices rememberMeServices =
                new PersistentTokenBasedRememberMeServices(SecurityConstant.REMEMBER_ME_KEY, userDetailsService, rememberMeTokenRepository);

        rememberMeServices.setParameter(SecurityConstant.REMEMBER_ME_PARAMETER);
        rememberMeServices.setTokenValiditySeconds(SecurityConstant.REMEMBER_ME_EXPIRE_SECONDS);
        return rememberMeServices;
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        TraditionAuthenticationFilter filter = new TraditionAuthenticationFilter(rememberMeServices());
        filter.setAuthenticationManager(http.getSharedObject(AuthenticationManager.class));
        filter.setAuthenticationFailureHandler(defaultAuthenticationFailureHandler);

        // 自动登录过滤器
        RememberMeAuthenticationFilter rememberMeAuthenticationFilter = new RememberMeAuthenticationFilter(authenticationManager, rememberMeServices());

        TraditionAuthenticationProvider provider = new TraditionAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);

        http.authenticationProvider(provider)
                .addFilterAfter(filter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(rememberMeAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
