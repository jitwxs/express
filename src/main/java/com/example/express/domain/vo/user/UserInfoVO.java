package com.example.express.domain.vo.user;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户信息VO
 * @author xiangsheng.wu
 * @date 2019年04月22日 13:05
 */
@Data
@Builder
public class UserInfoVO implements Serializable {
    private String username;
    /**
     * 性别
     */
    private String sex;
    /**
     * 性别ID
     */
    private String sexId;

    private String tel;
    /**
     * 学校名
     */
    private String school;

    private String studentIdCard;

    private String role;

    private String roleName;

    private String star;

    private String idCard;

    private String realName;
    /**
     * 能否切换角色（普通用户 --> 配送员）
     * 1：可以；0：不可以
     */
    private String canChangeRole;
    /**
     * 能否人脸登录
     * 1：可以；0：不可以
     */
    private String canFace;
}
