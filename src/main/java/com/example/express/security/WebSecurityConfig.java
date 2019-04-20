package com.example.express.security;

import com.example.express.common.constant.SecurityConstant;
import com.example.express.security.authentication.DefaultAuthenticationFailureHandler;
import com.example.express.security.authentication.DefaultUserDetailsServiceImpl;
import com.example.express.security.validate.code.ValidateCodeSecurityConfig;
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
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.sql.DataSource;

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
    private DefaultUserDetailsServiceImpl userDetailService;
    @Autowired
    private ValidateCodeSecurityConfig validateCodeSecurityConfig;
    @Autowired
    private SmsCodeAuthenticationSecurityConfig smsCodeAuthenticationSecurityConfig;
    @Autowired
    private ThidLoginAuthenticationSecurityConfig thidLoginAuthenticationSecurityConfig;
    @Autowired
    private DefaultAuthenticationFailureHandler defaultAuthenticationFailureHandler;
    @Autowired
    private DataSource dataSource;

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
     * token 持久化
     */
    @Bean
    public PersistentTokenRepository persistentTokenRepository(){
        JdbcTokenRepositoryImpl tokenRepository = new JdbcTokenRepositoryImpl();
        tokenRepository.setDataSource(dataSource);
        // 如果token表不存在，使用下面语句可以初始化该表；若存在，会报错。
//        tokenRepository.setCreateTableOnStartup(true);
        return tokenRepository;
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
                // 添加关于验证码登录的配置
                .apply(validateCodeSecurityConfig).and()
                .apply(smsCodeAuthenticationSecurityConfig).and()
                .apply(thidLoginAuthenticationSecurityConfig).and()
                // 设置登陆页
                .formLogin()
                    // 没有权限时跳转的Url
                    .loginPage(SecurityConstant.UN_AUTHENTICATION_URL)
                    // 默认登陆Url
                    .loginProcessingUrl(SecurityConstant.LOGIN_PROCESSING_URL_FORM)
                    // 设置登陆成功/失败处理逻辑
                    .defaultSuccessUrl(SecurityConstant.LOGIN_SUCCESS_URL)
                    .failureHandler(defaultAuthenticationFailureHandler)
                    .permitAll().and()
                .logout()
                    .logoutUrl(SecurityConstant.LOGOUT_URL)
                    .deleteCookies("JSESSIONID").and()
                .rememberMe()
                    .tokenRepository(persistentTokenRepository())
                    // 有效时间：单位s
                    .tokenValiditySeconds(60)
                    .userDetailsService(userDetailService).and()
                .authorizeRequests()
                    // 如果有允许匿名的url，填在下面
                    .antMatchers(
                            SecurityConstant.VALIDATE_CODE_URL_PREFIX + "/*",
                            SecurityConstant.THIRD_LOGIN_URL_PREFIX + "/*").permitAll()
                    .anyRequest()
                    .authenticated().and()
                // 关闭CSRF跨域
                .csrf().disable();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        // 设置拦截忽略文件夹，可以对静态资源放行
        web.ignoring().antMatchers("/assets/**");
    }
}
