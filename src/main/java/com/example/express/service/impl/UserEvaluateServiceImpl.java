package com.example.express.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.express.common.cache.CommonDataCache;
import com.example.express.common.util.DoubleUtils;
import com.example.express.common.util.StringUtils;
import com.example.express.domain.bean.OrderInfo;
import com.example.express.domain.bean.UserEvaluate;
import com.example.express.domain.enums.OrderStatusEnum;
import com.example.express.domain.enums.SysRoleEnum;
import com.example.express.mapper.UserEvaluateMapper;
import com.example.express.service.OrderInfoService;
import com.example.express.service.UserEvaluateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserEvaluateServiceImpl extends ServiceImpl<UserEvaluateMapper, UserEvaluate> implements UserEvaluateService {
    @Autowired
    private UserEvaluateMapper userEvaluateMapper;
    @Autowired
    private OrderInfoService orderInfoService;

    @Override
    public UserEvaluate getById(Serializable id) {
        UserEvaluate evaluate = super.getById(id);
        if(evaluate == null) {
            if(initUserEvaluate((String)id)) {
                evaluate = super.getById(id);
            }
        }
        return evaluate;
    }

    @Override
    public String getScoreFromCache(String userId) {
        return CommonDataCache.userScoreCache.get(userId);
    }

    @Override
    public boolean initUserEvaluate(String userId) {
        UserEvaluate evaluate = UserEvaluate.builder()
                .userId(userId)
                .score(new BigDecimal("10"))
                .count(0)
                .updateDate(LocalDateTime.now()).build();
        return this.retBool(userEvaluateMapper.insert(evaluate));
    }

    @Override
    public boolean updateEvaluate(String orderId, double score, SysRoleEnum roleEnum) {
        // 评分取值在0~10分
        if(score < 0 || score > 10) {
            return false;
        }
        // 取得用户ID
        String userId;
        OrderInfo info = orderInfoService.getById(orderId);
        switch (roleEnum) {
            case USER:
                userId = info.getUserId();
                break;
            case COURIER:
                userId = info.getCourierId();
                break;
            default:
                return false;
        }
        if(StringUtils.isBlank(userId)) {
            return false;
        }

        // 取得用户评分
        UserEvaluate evaluate = getById(userId);
        if(evaluate == null) {
            return false;
        }

        // score = (原始score * 基数 + 当前score) / (基数 + 1)
        double up = DoubleUtils.add(DoubleUtils.multiply(evaluate.getScore().doubleValue(), evaluate.getCount()), score);
        double result = DoubleUtils.divide(up, evaluate.getCount() + 1, 3);

        // 限制区间
        if(result < 0) {
            result = 0;
        }
        if(result > 10) {
            result = 10;
        }

        evaluate.setScore(new BigDecimal(result));
        evaluate.setCount(evaluate.getCount() + 1);

        return this.retBool(userEvaluateMapper.updateById(evaluate));
    }
}
