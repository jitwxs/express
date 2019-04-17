package com.example.express.domain.bean;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.example.express.domain.enums.SysRoleEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class SysUser {
    @TableId
    private Long id;

    private String username;

    private String password;
    /**
     * 用户角色
     */
    @TableField("role_id")
    @JsonValue
    private SysRoleEnum role;
    /**
     * 身份证号
     */
    private String idCard;
    /**
     * 学生证号
     */
    private String studentIdCard;

    private String tel;

    @TableLogic
    private Integer hasDelete;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createDate;

    @TableField(update = "now()")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateDate;
}
