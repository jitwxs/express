package com.example.express.security.validate.mobile;

import com.example.express.security.authentication.DefaultAuthenticationFailureHandler;
import com.example.express.security.authentication.MobileUserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

@Component
public class SmsCodeAuthenticationSecurityConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {
    @Autowired
    private MobileUserDetailsServiceImpl userDetailsService;
    @Autowired
    private DefaultAuthenticationFailureHandler defaultAuthenticationFailureHandler;

    @Override
    public void configure(HttpSecurity http) throws Exception {
        SmsCodeAuthenticationFilter filter = new SmsCodeAuthenticationFilter();
        filter.setAuthenticationManager(http.getSharedObject(AuthenticationManager.class));

        SmsCodeAuthenticationProvider smsCodeAuthenticationProvider = new SmsCodeAuthenticationProvider();
        smsCodeAuthenticationProvider.setUserDetailsService(userDetailsService);
        filter.setAuthenticationFailureHandler(defaultAuthenticationFailureHandler);

        http.authenticationProvider(smsCodeAuthenticationProvider)
                .addFilterAfter(filter, UsernamePasswordAuthenticationFilter.class);
    }
}
