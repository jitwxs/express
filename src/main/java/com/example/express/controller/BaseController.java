package com.example.express.controller;

import com.example.express.common.util.StringUtils;
import com.example.express.domain.bean.SysUser;
import com.example.express.domain.enums.SysRoleEnum;
import com.example.express.domain.enums.ThirdLoginTypeEnum;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;

@Controller
public class BaseController {
    /**
     * 读取Home页面URL
     * @date 2019/4/21 1:04
     */
    public String getHomePage(SysRoleEnum roleEnum) {
        switch (roleEnum) {
            case DIS_FORMAL:
                return "completeInfo";
            case USER:
                return "user/dashboard";
            case ADMIN:
                return "admin/dashboard";
            case COURIER:
                return "courier/dashboard";
            default:
                return "user/dashboard";
        }
    }

    protected void initModelMap(ModelMap map, SysUser sysUser) {
        /*
         * 获取显示用户名
         * 1. username字段
         * 2. tel字段
         * 3. 是否三方？三方登录用户
         * 4. 人脸登录用户
         */
        String username;
        if(StringUtils.isNotBlank(sysUser.getUsername())) {
            username = sysUser.getUsername();
        } else if(StringUtils.isNotBlank(sysUser.getTel())) {
            username = sysUser.getTel();
        } else if(sysUser.getThirdLogin() != ThirdLoginTypeEnum.NONE ){
            username = sysUser.getThirdLogin().getName() + "用户";
        } else {
            username = "人脸登录用户";
        }

        map.put("frontName", username);
    }
}
