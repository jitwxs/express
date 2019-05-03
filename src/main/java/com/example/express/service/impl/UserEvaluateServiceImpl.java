package com.example.express.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.express.domain.bean.UserEvaluate;
import com.example.express.mapper.UserEvaluateMapper;
import com.example.express.service.UserEvaluateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class UserEvaluateServiceImpl extends ServiceImpl<UserEvaluateMapper, UserEvaluate> implements UserEvaluateService {
    @Autowired
    private UserEvaluateMapper userEvaluateMapper;

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
    public boolean initUserEvaluate(String userId) {
        UserEvaluate evaluate = UserEvaluate.builder()
                .userId(userId)
                .score(new BigDecimal("0"))
                .count(0)
                .updateDate(LocalDateTime.now()).build();
        return this.retBool(userEvaluateMapper.insert(evaluate));
    }

    @Override
    public boolean updateUserEvaluate(String userId, double score) {
        // 评分取值在0~10分
        if(score < 0 || score > 10) {
            return false;
        }

        UserEvaluate evaluate = getById(userId);
        if(evaluate == null) {
            return false;
        }

        BigDecimal resultScore = new BigDecimal(score).add(evaluate.getScore().multiply(new BigDecimal(evaluate.getCount())));
        evaluate.setScore(resultScore);
        evaluate.setCount(evaluate.getCount() + 1);

        return this.retBool(userEvaluateMapper.updateById(evaluate));
    }
}
