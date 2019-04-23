package com.example.express.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.express.domain.bean.UserFeedback;
import com.example.express.domain.vo.UserFeedbackVO;
import com.example.express.mapper.UserFeedbackMapper;
import com.example.express.service.UserFeedbackService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserFeedbackServiceImpl extends ServiceImpl<UserFeedbackMapper, UserFeedback> implements UserFeedbackService {
    @Autowired
    private UserFeedbackMapper userFeedbackMapper;

    @Override
    public Page<UserFeedbackVO> pageUserFeedback(Page page, Integer type, Integer status, String userId) {
        QueryWrapper<UserFeedback> wrapper = new QueryWrapper<>();
        if(type != null) {
            wrapper.eq("type", type);
        }
        if(status != null) {
            wrapper.eq("status", status);
        }
        if(userId != null) {
            wrapper.eq("user_id", userId);
        }

        IPage selectPage = userFeedbackMapper.selectPage(page, wrapper);

        IPage<UserFeedbackVO> resultPage = new Page<>();
        BeanUtils.copyProperties(selectPage, resultPage);


    }
}
