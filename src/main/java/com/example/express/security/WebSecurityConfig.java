package com.example.express.security;

import com.example.express.common.constant.SecurityConstant;
import com.example.express.security.handler.DefaultAuthenticationFailureHandler;
import com.example.express.security.validate.tradition.TraditionUserDetailsService;
import com.example.express.security.validate.tradition.TraditionSecurityConfig;
import com.example.express.security.validate.mobile.SmsCodeAuthenticationSecurityConfig;
import com.example.express.security.validate.third.ThidLoginAuthenticationSecurityConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Spring Security 核心配置类
 * @author jitwxs
 * @since 2019/1/8 23:28
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private TraditionUserDetailsService userDetailService;
    @Autowired
    private TraditionSecurityConfig traditionSecurityConfig;
    @Autowired
    private SmsCodeAuthenticationSecurityConfig smsCodeAuthenticationSecurityConfig;
    @Autowired
    private ThidLoginAuthenticationSecurityConfig thidLoginAuthenticationSecurityConfig;
    @Autowired
    private DefaultAuthenticationFailureHandler defaultAuthenticationFailureHandler;

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 配置密码加密方式，这里选择不加密
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailService).passwordEncoder(passwordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                // 不同登陆方式的配置
                .apply(traditionSecurityConfig).and()
                .apply(smsCodeAuthenticationSecurityConfig).and()
                .apply(thidLoginAuthenticationSecurityConfig).and()
                // 设置登陆页
                .formLogin()
                    // 没有权限时跳转的Url
                    .loginPage(SecurityConstant.UN_AUTHENTICATION_URL)
                    // 设置登陆成功/失败处理逻辑
                    .defaultSuccessUrl(SecurityConstant.LOGIN_SUCCESS_URL)
                    .failureHandler(defaultAuthenticationFailureHandler)
                    .permitAll().and()
                .logout()
                    .logoutUrl(SecurityConstant.LOGOUT_URL)
                    .logoutSuccessUrl(SecurityConstant.UN_AUTHENTICATION_URL)
                    .deleteCookies("JSESSIONID").and()
                .rememberMe()
                    .key(SecurityConstant.REMEMBER_ME_KEY).and()
                .authorizeRequests()
                    // 如果有允许匿名的url，填在下面
                    .antMatchers(SecurityConstant.VALIDATE_CODE_URL_PREFIX + "/**").permitAll()
                    .antMatchers(SecurityConstant.THIRD_LOGIN_URL_PREFIX + "/**").permitAll()
                    .antMatchers("/register", "/auth/register", "/auth/face-check", SecurityConstant.LOGIN_PROCESSING_URL_FACE).permitAll()
                    .antMatchers("/api/v1/public/**").permitAll()
                    .anyRequest().authenticated().and()
                // 关闭CSRF跨域
                .csrf().disable();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        // 设置拦截忽略文件夹，可以对静态资源放行
        web.ignoring().antMatchers("/assets/**");
    }
}
