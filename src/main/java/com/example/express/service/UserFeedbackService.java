package com.example.express.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.express.domain.bean.UserFeedback;
import com.example.express.domain.enums.FeedbackTypeEnum;
import com.example.express.domain.vo.BootstrapTableVO;
import com.example.express.domain.vo.UserFeedbackDescVO;
import com.example.express.domain.vo.UserFeedbackVO;

public interface UserFeedbackService extends IService<UserFeedback> {
    /**
     * 分页查询前台反馈信息
     * @author jitwxs
     * @date 2019/4/23 23:09
     */
    BootstrapTableVO<UserFeedbackVO> pageUserFeedbackVO(Page<UserFeedback> page, QueryWrapper<UserFeedback> wrapper);

    boolean createFeedback(String userId, FeedbackTypeEnum feedbackTypeEnum, String content, String orderId);

    UserFeedbackDescVO getDescVO(Integer feedbackId);
}
