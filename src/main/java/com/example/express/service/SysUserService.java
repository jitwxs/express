package com.example.express.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.express.domain.bean.SysUser;

public interface SysUserService extends IService<SysUser> {
    SysUser getByName(String username);

    SysUser getByTel(String tel);

    boolean isExist(String username);
}
