package com.example.express.controller;

import com.example.express.domain.bean.ResponseResult;
import com.example.express.domain.bean.SysUser;
import com.example.express.domain.enums.ResponseErrorCodeEnum;
import com.example.express.domain.enums.SysRoleEnum;
import com.example.express.domain.vo.RegistryVO;
import com.example.express.service.SysUserService;
import com.example.express.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 登陆失败
     */
    @RequestMapping("/login/error")
    public ResponseResult loginError(HttpServletRequest request) {
        AuthenticationException exception = (AuthenticationException)request.getSession().
                getAttribute("SPRING_SECURITY_LAST_EXCEPTION");

        return ResponseResult.failure(ResponseErrorCodeEnum.SYSTEM_ERROR.getCode(), exception.toString());
    }

    /**
     * 用户注册
     * @date 2019/4/17 0:06
     */
    @PostMapping("/register")
    public ResponseResult register(@RequestBody RegistryVO registryVO) {
        // 校验type
        SysRoleEnum roleEnum = SysRoleEnum.getByName(registryVO.getType());
        if (roleEnum == null) {
            return ResponseResult.failure(ResponseErrorCodeEnum.ROLE_ERROR);
        }
        // 校验用户名是否存在
        if (sysUserService.isExist(registryVO.getUsername())) {
            return ResponseResult.failure(ResponseErrorCodeEnum.USERNAME_EXIST_ERROR);
        }
        // 校验type
        if(!StringUtils.isValidTel(registryVO.getTel())) {
            return ResponseResult.failure(ResponseErrorCodeEnum.TEL_NOT_LEGAL);
        }

        SysUser sysUser = SysUser.builder()
                .username(registryVO.getUsername())
                .password(passwordEncoder.encode(registryVO.getPassword()))
                .tel(registryVO.getTel())
                .role(roleEnum).build();

        if(sysUserService.save(sysUser)) {
            return ResponseResult.success();
        } else {
            return ResponseResult.failure(ResponseErrorCodeEnum.OPERATION_ERROR);
        }
    }
}
