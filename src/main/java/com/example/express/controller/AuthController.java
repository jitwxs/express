package com.example.express.controller;

import com.example.express.domain.bean.ResponseResult;
import com.example.express.domain.bean.SysUser;
import com.example.express.domain.enums.ResponseErrorCodeEnum;
import com.example.express.domain.enums.SysRoleEnum;
import com.example.express.domain.vo.RegistryVO;
import com.example.express.security.SecurityConstants;
import com.example.express.service.SysUserService;
import com.example.express.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
public class AuthController {
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 获取短信验证码
     * @date 2019/4/17 22:40
     */
    @GetMapping(SecurityConstants.VALIDATE_CODE_URL_PREFIX + "/sms")
    public String sms(String mobile, HttpSession session) {
        int code = (int) Math.ceil(Math.random() * 9000 + 1000);

        Map<String, String> map = new HashMap<>(16);
        map.put("mobile", mobile);
        map.put("code", String.valueOf(code));

        session.setAttribute("smsCode", map);

        log.info("{}：为 {} 设置短信验证码：{}", session.getId(), mobile, code);

        return "发送成功";
    }

    /**
     * 用户注册
     * @date 2019/4/17 0:06
     */
    @PostMapping("/auth/register")
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
