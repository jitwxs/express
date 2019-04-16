package com.example.express.security;

import com.example.express.domain.bean.SysUser;
import com.example.express.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author jitwxs
 * @date 2018/3/30 9:17
 */
@Service("userDetailsService")
public class CustomUserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private SysUserService sysUserService;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        Collection<GrantedAuthority> authorities = new ArrayList<>();

        SysUser sysUser = sysUserService.getByName(s);
        if(sysUser == null) {
            throw new UsernameNotFoundException("用户名不存在");
        }

        // 添加权限
        authorities.add(new SimpleGrantedAuthority(sysUser.getRole().getName()));

        // 返回UserDetails实现类
        return new User(sysUser.getUsername(), sysUser.getPassword(), authorities);
    }
}
