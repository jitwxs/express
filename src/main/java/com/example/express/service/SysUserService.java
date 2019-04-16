package com.example.express.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.express.domain.bean.ResponseResult;
import com.example.express.domain.bean.SysUser;

public interface SysUserService extends IService<SysUser> {
    SysUser getByName(String username);

    boolean isExist(String username);
}
