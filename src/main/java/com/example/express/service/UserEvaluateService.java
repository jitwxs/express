package com.example.express.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.express.domain.bean.UserEvaluate;
import com.example.express.domain.enums.SysRoleEnum;

public interface UserEvaluateService extends IService<UserEvaluate> {
    String getScoreFromCache(String userId);

    boolean initUserEvaluate(String userId);
    /**
     * 更新评分
     * @param roleEnum 用户或派送员
     */
    boolean updateEvaluate(String orderId, double score, SysRoleEnum roleEnum);
}
