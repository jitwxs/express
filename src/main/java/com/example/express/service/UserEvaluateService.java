package com.example.express.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.express.domain.bean.UserEvaluate;

public interface UserEvaluateService extends IService<UserEvaluate> {
    boolean initUserEvaluate(String userId);

    boolean updateUserEvaluate(String userId, double score);
}
