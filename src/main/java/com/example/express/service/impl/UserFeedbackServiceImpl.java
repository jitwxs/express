package com.example.express.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.express.common.util.CollectionUtils;
import com.example.express.common.util.RandomUtils;
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
    public BootstrapTableVO<UserFeedbackVO> pageUserFeedbackVO(Page<UserFeedback> page, QueryWrapper<UserFeedback> wrapper) {

        IPage<UserFeedback> selectPage = userFeedbackMapper.selectPage(page, wrapper);

        BootstrapTableVO<UserFeedbackVO> vo = new BootstrapTableVO<>();
        vo.setTotal(selectPage.getTotal());
        vo.setRows(convert(selectPage.getRecords()));

        return vo;
    }

    @Override
    public boolean createFeedback(String userId, FeedbackTypeEnum feedbackTypeEnum, String content, String orderId) {
        UserFeedback feedback = UserFeedback.builder()
                .id(RandomUtils.time())
                .userId(userId)
                .feedbackType(feedbackTypeEnum)
                .feedbackStatus(FeedbackStatusEnum.WAIT)
                .content(content).build();
        if(StringUtils.isNotBlank(orderId)) {
            feedback.setOrderId(orderId);
        }

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
                .createDate(userFeedback.getCreateDate())
                .updateDate(userFeedback.getUpdateDate()).build();

        if(StringUtils.isNotBlank(userFeedback.getHandler())) {
            String handlerName = sysUserService.getFrontName(userFeedback.getHandler());
            vo.setHandlerName(handlerName);
            vo.setResult(userFeedback.getResult());
        }

        return vo;
    }
}
