package com.example.express.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.express.domain.bean.SysUser;
import com.example.express.domain.enums.ResponseErrorCodeEnum;
import com.example.express.domain.enums.SysRoleEnum;
import com.example.express.domain.enums.ThirdLoginTypeEnum;
import com.example.express.exception.CustomException;
import com.example.express.mapper.SysUserMapper;
import com.example.express.service.SysUserService;
import com.example.express.common.util.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {
    @Autowired
    private SysUserMapper sysUserMapper;

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

    private boolean registryByThirdLogin(String thirdLoginId, ThirdLoginTypeEnum thirdLoginTypeEnum) {
        SysUser user = SysUser.builder()
                .thirdLogin(thirdLoginTypeEnum)
                .thirdLoginId(thirdLoginId)
                .role(SysRoleEnum.USER).build();
        int i = sysUserMapper.insert(user);

        return i == 1;
    }
}
