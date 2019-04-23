package com.example.express.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.express.common.util.CollectionUtils;
import com.example.express.common.util.StringUtils;
import com.example.express.domain.bean.UserFeedback;
import com.example.express.domain.enums.FeedbackStatusEnum;
import com.example.express.domain.enums.FeedbackTypeEnum;
import com.example.express.domain.vo.BootstrapTableVO;
import com.example.express.domain.vo.UserFeedbackDescVO;
import com.example.express.domain.vo.UserFeedbackVO;
import com.example.express.mapper.UserFeedbackMapper;
import com.example.express.service.OrderInfoService;
import com.example.express.service.SysUserService;
import com.example.express.service.UserFeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserFeedbackServiceImpl extends ServiceImpl<UserFeedbackMapper, UserFeedback> implements UserFeedbackService {
    @Autowired
    private UserFeedbackMapper userFeedbackMapper;
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private OrderInfoService orderInfoService;

    @Override
    public BootstrapTableVO pageUserFeedback(Page<UserFeedback> page, Integer type, Integer status, String userId) {
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

        IPage<UserFeedback> selectPage = userFeedbackMapper.selectPage(page, wrapper);

        return BootstrapTableVO.builder()
                .total(selectPage.getTotal())
                .rows(convert(selectPage.getRecords())).build();
    }

    @Override
    public boolean createFeedback(String userId, FeedbackTypeEnum feedbackTypeEnum, String content, String orderId) {
        UserFeedback feedback = UserFeedback.builder()
                .userId(userId)
                .feedbackType(feedbackTypeEnum)
                .feedbackStatus(FeedbackStatusEnum.WAIT)
                .content(content)
                .orderId(orderId).build();

        return this.retBool(userFeedbackMapper.insert(feedback));
    }

    @Override
    public UserFeedbackDescVO getDescVO(Integer feedbackId) {
        UserFeedback feedback = userFeedbackMapper.selectById(feedbackId);
        if(feedback == null) {
            return new UserFeedbackDescVO();
        }

        String userFrontName = sysUserService.getFrontName(feedback.getUserId());
        String handlerFrontName = null;
        if(feedback.getHandler() != null) {
            handlerFrontName = sysUserService.getFrontName(feedback.getHandler());
        }

        UserFeedbackDescVO vo = UserFeedbackDescVO.builder()
                .id(feedback.getId())
                .frontName(userFrontName)
                .feedbackType(feedback.getFeedbackType().getName())
                .feedbackStatus(feedback.getFeedbackStatus().getName())
                .content(feedback.getContent())
                .handler(handlerFrontName)
                .result(feedback.getResult())
                .createDate(feedback.getCreateDate())
                .updateDate(feedback.getUpdateDate()).build();

        if(StringUtils.isNotBlank(feedback.getOrderId())) {
            vo.setOrder(orderInfoService.getDescVO(feedback.getOrderId()));
        }

        return vo;
    }

    private List<UserFeedbackVO> convert(List<UserFeedback> userFeedbacks) {
        if (CollectionUtils.isListEmpty(userFeedbacks)) {
            return Collections.emptyList();
        }
        return userFeedbacks.stream().map(this::convert).collect(Collectors.toList());
    }

    private UserFeedbackVO convert(UserFeedback userFeedback) {
        String frontName = sysUserService.getFrontName(userFeedback.getUserId());

        UserFeedbackVO vo = UserFeedbackVO.builder()
                .id(userFeedback.getId())
                .frontName(frontName)
                .type(userFeedback.getFeedbackType().getType())
                .status(userFeedback.getFeedbackStatus().getStatus())
                .content(userFeedback.getContent())
                .result(userFeedback.getResult())
                .createDate(userFeedback.getCreateDate()).build();

        return vo;
    }
}
