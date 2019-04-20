package com.example.express.domain.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.example.express.domain.enums.SysRoleEnum;
import com.example.express.domain.enums.ThirdLoginTypeEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Builder;
import lombok.Data;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

@Data
@Builder
public class SysUser implements UserDetails, CredentialsContainer {
    @TableId(type = IdType.UUID)
    private String id;

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
    /**
     * 三方登陆类型
     */
    @TableField("third_login_type")
    @JsonValue
    private ThirdLoginTypeEnum thirdLogin;
    /**
     * 三方登陆ID
     */
    private String thirdLoginId;
    /**
     * 逻辑删除
     */
    @TableLogic
    private Integer hasDelete;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createDate;

    @TableField(update = "now()")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateDate;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return new ArrayList<GrantedAuthority>(){{
           add(new SimpleGrantedAuthority(getRole().getName()));
        }};
    }

    /**
     * 用户是否过期
     */
    @Override
    public boolean isAccountNonExpired() {
        return hasDelete == 1;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public void eraseCredentials() {
        this.password = null;
    }
}
