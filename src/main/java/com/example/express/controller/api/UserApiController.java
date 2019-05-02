package com.example.express.controller.api;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.express.common.util.StringUtils;
import com.example.express.domain.ResponseResult;
import com.example.express.domain.bean.SysUser;
import com.example.express.domain.enums.ResponseErrorCodeEnum;
import com.example.express.domain.vo.BootstrapTableVO;
import com.example.express.domain.vo.admin.AdminUserInfoVO;
import com.example.express.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * 用户信息 API Controller
 * @author jitwxs
 * @date 2019年05月02日 12:18
 */
@RestController
@RequestMapping("/api/v1/user")
public class UserApiController {
    @Autowired
    private SysUserService sysUserService;

    /**
     * 获取用户列表
     * @param id 用户ID
     * @param isReal 是否实名认证
     * @param isEnable 是否启用
     * @param isLock 是否冻结
     * @param isThird 是否绑定三方登录
     * @param username 用户名
     * @param tel 手机号
     * @author jitwxs
     * @date 2019/5/2 12:24
     */
    @GetMapping("/list")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public BootstrapTableVO<AdminUserInfoVO> listUser(@RequestParam(required = false, defaultValue = "1") Integer current,
                                                      @RequestParam(required = false, defaultValue = "10") Integer size,
                                                      String isReal, String isEnable, String isLock, String isThird,
                                                      String id,  String username, String tel) {
        QueryWrapper<SysUser> wrapper = new QueryWrapper<>();
        Integer enable = StringUtils.toInteger(isEnable, -1);
        if (enable != -1) {
            wrapper.eq("has_enable", enable);
        }

        Integer lock = StringUtils.toInteger(isLock, -1);
        if (lock == 1) {
            wrapper.ge("lock_date", LocalDateTime.now());
        } else if (lock == 0) {
            wrapper.isNull("lock_date").or().le("lock_date", LocalDateTime.now());
        }

        Integer real = StringUtils.toInteger(isReal, -1);
        if (real == 1) {
            wrapper.isNotNull("real_name").isNotNull("id_card");
        } else if (real == 0) {
            wrapper.isNull("real_name").or().isNull("id_card");
        }

        Integer third = StringUtils.toInteger(isThird, -1);
        if (third != -1) {
            wrapper.eq("third_login_type", third);
        }

        if (StringUtils.isNotBlank(id)) {
            wrapper.eq("id", id);
        }
        if (StringUtils.isNotBlank(username)) {
            wrapper.like("username", username);
        }
        if (StringUtils.isNotBlank(tel)) {
            wrapper.eq("tel", tel);
        }

        return sysUserService.pageAdminUserInfoVO(new Page<>(current, size), wrapper);
    }

    /**
     * 改变用户状态
     * @param type 1. 禁用；2：启用；3：冻结；4：解冻
     * @param hour 冻结小时数
     * @author jitwxs
     * @date 2019/5/2 13:50
     */
    @PostMapping("/{id}/status")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseResult changeStatus(@PathVariable String id, String type, String hour) {
        Integer op = StringUtils.toInteger(type, -1);

        SysUser user = sysUserService.getById(id);
        switch (op) {
            case 1:
                user.setHasEnable(0);
                break;
            case 2:
                user.setHasEnable(1);
                break;
            case 3:
                Integer lockHour = StringUtils.toInteger(hour, -1);
                if(lockHour == -1) {
                    return ResponseResult.failure(ResponseErrorCodeEnum.MUST_NUMBER);
                }
                user.setLockDate(LocalDateTime.now().plusHours(lockHour));
                break;
            case 4:
                user.setLockDate(LocalDateTime.now());
                break;
            default:
                return ResponseResult.failure(ResponseErrorCodeEnum.PARAMETER_ERROR);
        }

        if(sysUserService.updateById(user)) {
            return ResponseResult.success();
        } else {
            return ResponseResult.failure(ResponseErrorCodeEnum.OPERATION_ERROR);
        }
    }
}
