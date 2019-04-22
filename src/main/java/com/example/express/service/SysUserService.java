package com.example.express.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.express.domain.ResponseResult;
import com.example.express.domain.bean.SysUser;
import com.example.express.domain.enums.ThirdLoginTypeEnum;
import com.example.express.domain.vo.UserInfoVO;

import javax.servlet.http.HttpSession;

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
     * 获取用户信息
     */
    UserInfoVO getUserInfo(SysUser user);
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
    /**
     * 根据用户名注册
     * @author jitwxs
     * @date 2019/4/22 0:39
     */
    ResponseResult registryByUsername(String username, String password);
    /**
     * 根据手机号注册
     * @author jitwxs
     * @date 2019/4/22 0:39
     */
    ResponseResult registryBTel(String tel, String code, HttpSession session);
    /**
     * 修改密码
     */
    ResponseResult resetPassword(String userId, String oldPassword, String newPassword);
    /**
     * 设置用户名密码
     */
    ResponseResult setUsernameAndPassword(SysUser user, String username, String password);
    /**
     * 设置实名信息
     */
    ResponseResult setRealName(SysUser user, String realName, String idCard);
    /**
     * 设置/修改手机号码
     */
    ResponseResult setTel(SysUser user, String tel, String code, HttpSession session);
    /**
     * 设置/修改高校信息
     * @param schoolId 学校ID
     * @param studentIdCard 学号
     */
    ResponseResult setSchoolInfo(SysUser user, Integer schoolId, String studentIdCard);
    /**
     * 切换角色
     * user -> courier
     */
    ResponseResult changeRole(SysUser user);
}
