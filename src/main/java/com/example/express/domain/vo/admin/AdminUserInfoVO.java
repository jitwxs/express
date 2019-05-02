package com.example.express.domain.vo.admin;

import com.example.express.common.util.CollectionUtils;
import com.example.express.common.util.StringUtils;
import com.example.express.domain.bean.SysUser;
import com.example.express.domain.enums.ThirdLoginTypeEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户信息VO，管理员用
 * @author jitwxs
 * @date 2019年05月02日 12:29
 */
@Data
@Builder
public class AdminUserInfoVO implements Serializable {
    private String id;

    private String username;

    private String tel;

    private Integer role;
    /**
     * 是否实名认证
     */
    private Boolean hasReal;
    /**
     * 是否启用
     */
    private Boolean hasEnable;
    /**
     * 账户解冻时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime lockDate;
    /**
     * 三方登录类型
     */
    private String thirdLogin;
    /**
     * 注册时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createDate;

    public static List<AdminUserInfoVO> convert(List<SysUser> users) {
        if(CollectionUtils.isListEmpty(users)) {
            return Collections.emptyList();
        }

        return users.stream().map(AdminUserInfoVO::convert).collect(Collectors.toList());
    }

    public static AdminUserInfoVO convert(SysUser user) {
        AdminUserInfoVO vo = AdminUserInfoVO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .tel(user.getTel())
                .role(user.getRole().getType())
                .hasReal(!StringUtils.isAnyBlank(user.getRealName(), user.getId()))
                .hasEnable(user.getHasEnable() == 1)
                .createDate(user.getCreateDate()).build();

        if(user.getThirdLogin() != ThirdLoginTypeEnum.NONE) {
            vo.setThirdLogin(user.getThirdLogin().getName());
        }

        LocalDateTime lockDate = user.getLockDate();
        if(lockDate != null && LocalDateTime.now().isBefore(lockDate)) {
            vo.setLockDate(lockDate);
        }

        return vo;
    }
}
