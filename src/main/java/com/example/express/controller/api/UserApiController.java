package com.example.express.controller.api;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.express.common.util.StringUtils;
import com.example.express.domain.ResponseResult;
import com.example.express.domain.bean.SysUser;
import com.example.express.domain.enums.ResponseErrorCodeEnum;
import com.example.express.domain.enums.SysRoleEnum;
import com.example.express.domain.vo.BootstrapTableVO;
import com.example.express.domain.vo.admin.AdminUserInfoVO;
import com.example.express.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                                                      String isReal, String isEnable, String isLock, String role,
                                                      String id,  String username, String tel, @AuthenticationPrincipal SysUser sysUser) {
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

        Integer roleType = StringUtils.toInteger(role, -2);
        SysRoleEnum roleEnum = SysRoleEnum.getByType(roleType);
        if (roleEnum != null) {
            wrapper.eq("role_id", roleType);
        }

        if (StringUtils.isNotBlank(id)) {
            wrapper.eq("id", id);
        } else{
            // 不显示当前用户
            wrapper.ne("id", sysUser.getId());
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
     * 获取配送员列表
     * @author jitwxs
     * @date 2019/5/3 21:58
     */
    @GetMapping("/courier-list")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseResult listCourier() {
        List<SysUser> users = sysUserService.list(new QueryWrapper<SysUser>().eq("role_id", SysRoleEnum.COURIER.getType()));
        if(users.size() == 0) {
            return ResponseResult.success();
        }

        List<Map> result = new ArrayList<>();
        for(SysUser user : users) {
            Map<String ,String> map = new HashMap<>();
            map.put("id", user.getId());
            map.put("name", sysUserService.getFrontName(user));
            result.add(map);
        }

        return ResponseResult.success(result);
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
        if(user.getRole() == SysRoleEnum.ADMIN) {
            return ResponseResult.failure(ResponseErrorCodeEnum.NO_PERMISSION);
        }

        switch (op) {
            case 1:
                user.setHasEnable(0);
                break;
            case 2:
                user.setHasEnable(1);
                break;
            case 3:
                // 必须为正整数
                Integer lockHour = StringUtils.toInteger(hour, -1);
                if(lockHour == -1 || lockHour < 0) {
                    return ResponseResult.failure(ResponseErrorCodeEnum.MUST_POSITIVE_INTEGER);
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
