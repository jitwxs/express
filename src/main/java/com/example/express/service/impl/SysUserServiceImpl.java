package com.example.express.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.express.common.util.CollectionUtils;
import com.example.express.common.util.StringUtils;
import com.example.express.domain.ResponseResult;
import com.example.express.domain.bean.DataSchool;
import com.example.express.domain.bean.SysUser;
import com.example.express.domain.enums.ResponseErrorCodeEnum;
import com.example.express.domain.enums.SysRoleEnum;
import com.example.express.domain.enums.ThirdLoginTypeEnum;
import com.example.express.domain.vo.UserInfoVO;
import com.example.express.exception.CustomException;
import com.example.express.mapper.SysUserMapper;
import com.example.express.service.DataSchoolService;
import com.example.express.service.SmsService;
import com.example.express.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.util.List;

@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {
    @Autowired
    private SysUserMapper sysUserMapper;
    @Autowired
    private SmsService smsService;
    @Autowired
    private DataSchoolService dataSchoolService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public SysUser getByName(String username) {
        List<SysUser> userList = sysUserMapper.selectList(new QueryWrapper<SysUser>().eq("username", username));

        return CollectionUtils.getListFirst(userList);
    }

    @Override
    public SysUser getByTel(String tel) {
        List<SysUser> userList = sysUserMapper.selectList(new QueryWrapper<SysUser>().eq("tel", tel));

        return CollectionUtils.getListFirst(userList);
    }

    @Override
    public SysUser getByThirdLogin(String thirdLoginId, ThirdLoginTypeEnum thirdLoginTypeEnum) {
        if(thirdLoginTypeEnum == ThirdLoginTypeEnum.NONE) {
            return null;
        }

        List<SysUser> userList = sysUserMapper.selectList(new QueryWrapper<SysUser>()
                .eq("third_login_type", thirdLoginTypeEnum.getType())
                .eq("third_login_id", thirdLoginId));

        return CollectionUtils.getListFirst(userList);
    }

    @Override
    public UserInfoVO getUserInfo(SysUser user) {
        UserInfoVO vo = UserInfoVO.builder()
                .username(user.getUsername())
                .sex(String.valueOf(user.getSex().getType()))
                .tel(user.getTel())
                .studentIdCard(user.getStudentIdCard())
                .role(user.getRole().getCnName())
                .star(user.getStar())
                .idCard(user.getIdCard())
                .realName(user.getRealName()).build();

        DataSchool school = dataSchoolService.getById(user.getSchoolId());
        if(school != null) {
            vo.setSchool(school.getName());
        }

        return vo;
    }

    @Override
    public boolean checkExistByTel(String mobile) {
        return getByTel(mobile) != null;
    }

    @Override
    public boolean checkExistByUsername(String username) {
        return getByName(username) != null;
    }

    @Override
    @Transactional(rollbackFor = CustomException.class)
    public SysUser thirdLogin(String thirdLoginId, ThirdLoginTypeEnum thirdLoginTypeEnum) {
        SysUser sysUser = getByThirdLogin(thirdLoginId, thirdLoginTypeEnum);
        if(sysUser == null) {
            // 三方注册
            if(!registryByThirdLogin(thirdLoginId, thirdLoginTypeEnum)) {
                throw new CustomException(ResponseErrorCodeEnum.REGISTRY_ERROR);
            }
            sysUser = getByThirdLogin(thirdLoginId, thirdLoginTypeEnum);
            if(sysUser == null) {
                throw new CustomException(ResponseErrorCodeEnum.THIRD_LOGIN_ERROR);
            }
        }

        return sysUser;
    }

    @Transactional(rollbackFor = CustomException.class)
    @Override
    public ResponseResult registryByUsername(String username, String password) {
        if(checkExistByUsername(username)) {
            return ResponseResult.failure(ResponseErrorCodeEnum.USERNAME_EXIST_ERROR);
        }

        SysUser user = SysUser.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .role(SysRoleEnum.DIS_FORMAL).build();

        if(!this.retBool(sysUserMapper.insert(user))) {
            return ResponseResult.failure(ResponseErrorCodeEnum.REGISTER_ERROR);
        }
        return ResponseResult.success();
    }

    @Override
    public ResponseResult registryBTel(String tel, String code, HttpSession session) {
        ResponseErrorCodeEnum codeEnum = smsService.check(session, tel, code);
        if(codeEnum != ResponseErrorCodeEnum.SUCCESS) {
            return ResponseResult.failure(codeEnum);
        }

        SysUser user = SysUser.builder()
                .tel(tel)
                .role(SysRoleEnum.DIS_FORMAL).build();

        if(!this.retBool(sysUserMapper.insert(user))) {
            return ResponseResult.failure(ResponseErrorCodeEnum.REGISTER_ERROR);
        }
        return ResponseResult.success();
    }

    private boolean registryByThirdLogin(String thirdLoginId, ThirdLoginTypeEnum thirdLoginTypeEnum) {
        SysUser user = SysUser.builder()
                .thirdLogin(thirdLoginTypeEnum)
                .thirdLoginId(thirdLoginId)
                .role(SysRoleEnum.DIS_FORMAL).build();

        return this.retBool(sysUserMapper.insert(user));
    }
}
