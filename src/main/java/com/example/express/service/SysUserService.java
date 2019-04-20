package com.example.express.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.express.domain.bean.SysUser;
import com.example.express.domain.enums.ThirdLoginTypeEnum;

public interface SysUserService extends IService<SysUser> {
    /**
     * 根据用户名查找用户
     */
    SysUser getByName(String username);
    /**
     * 根据手机号查找用户
     */
    SysUser getByTel(String tel);
    /**
     * 根据三方登陆查找用户
     */
    SysUser getByThirdLogin(String thirdLoginId, ThirdLoginTypeEnum thirdLoginTypeEnum);
    /**
     * 根据用户名判断用户是否存在
     */
    boolean checkExistByUsername(String username);
    /**
     * 根据手机号判断用户是否存在
     */
    boolean checkExistByTel(String mobile);
    /**
     * 三方登陆逻辑
     */
    SysUser thirdLogin(String thirdLoginId, ThirdLoginTypeEnum thirdLoginTypeEnum);
}
