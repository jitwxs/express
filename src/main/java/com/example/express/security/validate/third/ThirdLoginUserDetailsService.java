package com.example.express.security.validate.third;

import com.example.express.domain.bean.SysUser;
import com.example.express.domain.enums.ResponseErrorCodeEnum;
import com.example.express.security.exception.DefaultAuthException;
import com.example.express.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;

/**
 * 三方登陆 UserDetailService，通过用户ID读取信息
 * @author jitwxs
 * @since 2019/1/8 23:37
 */
@Service
public class ThirdLoginUserDetailsService implements UserDetailsService {
    @Autowired
    private SysUserService userService;

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        // 从数据库中取出用户信息
        SysUser user = userService.getById(userId);

        // 判断用户是否存在
        if (user == null) {
            throw new DefaultAuthException(ResponseErrorCodeEnum.USER_NOT_EXIST);
        }

        // 添加权限
        authorities.add(new SimpleGrantedAuthority(user.getRole().getName()));

        return user;
    }
}
