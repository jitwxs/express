package com.example.express.controller;

import com.example.express.common.util.StringUtils;
import com.example.express.domain.bean.SysUser;
import com.example.express.domain.enums.SysRoleEnum;
import org.springframework.stereotype.Controller;

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

    /**
     * 获取用户名
     * 默认读取username，不存在读取tel，不存在返回空
     * @author jitwxs
     * @date 2019/4/21 1:06
     */
    public String getUsername(SysUser sysUser) {
        if(StringUtils.isNotBlank(sysUser.getUsername())) {
            return sysUser.getUsername();
        } else if(StringUtils.isNotBlank(sysUser.getTel())) {
            return sysUser.getTel();
        }
        return StringUtils.EMPTY;
    }
}
