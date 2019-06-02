package com.example.express.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.express.common.util.StringUtils;
import com.example.express.domain.ResponseResult;
import com.example.express.domain.bean.OrderEvaluate;
import com.example.express.domain.bean.OrderInfo;
import com.example.express.domain.enums.OrderStatusEnum;
import com.example.express.domain.enums.ResponseErrorCodeEnum;
import com.example.express.domain.enums.SysRoleEnum;
import com.example.express.mapper.OrderEvaluateMapper;
import com.example.express.service.OrderEvaluateService;
import com.example.express.service.OrderInfoService;
import com.example.express.service.UserEvaluateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class OrderEvaluateServiceImpl extends ServiceImpl<OrderEvaluateMapper, OrderEvaluate> implements OrderEvaluateService {
    @Autowired
    private OrderEvaluateMapper orderEvaluateMapper;
    @Autowired
    private OrderInfoService orderInfoService;
    @Autowired
    private UserEvaluateService userEvaluateService;
    @Autowired
    private DataSourceTransactionManager transactionManager;

    @Override
    public OrderEvaluate getById(Serializable id) {
        OrderEvaluate evaluate = super.getById(id);
        if(evaluate == null) {
            if(initOrderEvaluate((String)id)) {
                evaluate = super.getById(id);
            }
        }
        return evaluate;
    }

    @Override
    public boolean initOrderEvaluate(String orderId) {
        OrderInfo orderInfo = orderInfoService.getById(orderId);
        if(orderInfo == null) {
            return false;
        }

        OrderEvaluate evaluate = new OrderEvaluate();
        evaluate.setId(orderId);
        if(orderInfo.getOrderStatus() == OrderStatusEnum.COMPLETE || orderInfo.getOrderStatus() == OrderStatusEnum.ERROR) {
            evaluate.setHasOpen(true);
        } else {
            evaluate.setHasOpen(false);
        }
        evaluate.setUpdateDate(LocalDateTime.now());

        return this.retBool(orderEvaluateMapper.insert(evaluate));
    }

    @Override
    public boolean changEvaluateStatus(String orderId, boolean isOpen) {
        OrderEvaluate evaluate = super.getById(orderId);
        evaluate.setHasOpen(isOpen);

        return this.retBool(orderEvaluateMapper.updateById(evaluate));
    }

    @Override
    public boolean canEvaluate(String orderId, String userId, SysRoleEnum roleEnum) {
        OrderEvaluate evaluate = getById(orderId);
        if(evaluate == null || !evaluate.getHasOpen()) {
            return false;
        }

        // 只支持用户、快递员，查询自己订单是否可以评价
        if(roleEnum == SysRoleEnum.USER) {
            if(!orderInfoService.isUserOrder(orderId, userId)) {
                return false;
            }
            return evaluate.getUserScore() == null;

        } else if(roleEnum == SysRoleEnum.COURIER) {
            if(!orderInfoService.isCourierOrder(orderId, userId)) {
                return false;
            }
            return evaluate.getCourierScore() == null;
        }

        return false;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public ResponseResult userEvaluate(String orderId, String userId, double score, String text) {
        DefaultTransactionDefinition definition = new DefaultTransactionDefinition();
        TransactionStatus status = transactionManager.getTransaction(definition);

        // 校验评分
        if(!isLegalScore(score)) {
            transactionManager.rollback(status);
            return ResponseResult.failure(ResponseErrorCodeEnum.EVALUATE_SCORE_ERROR);
        }

        OrderEvaluate evaluate = super.getById(orderId);

        // 未开启评分
        if(!evaluate.getHasOpen()) {
            transactionManager.rollback(status);
            return ResponseResult.failure(ResponseErrorCodeEnum.ORDER_NOT_OPEN_EVALUATE);
        }
        // 是否有权限
        if(!orderInfoService.isUserOrder(orderId, userId)) {
            transactionManager.rollback(status);
            return ResponseResult.failure(ResponseErrorCodeEnum.NO_PERMISSION);
        }
        // 已评分
        if(evaluate.getUserScore() != null) {
            transactionManager.rollback(status);
            return ResponseResult.failure(ResponseErrorCodeEnum.ORDER_ALREADY_EVALUATE);
        }

        // text超过最大
        if(StringUtils.isNotBlank(text) && text.length() > 255) {
            transactionManager.rollback(status);
            return ResponseResult.failure(ResponseErrorCodeEnum.STR_LENGTH_OVER, new Object[]{"评价", 255});
        }

        /* 订单评分 */
        evaluate.setUserId(userId);
        evaluate.setUserScore(new BigDecimal(score));
        evaluate.setUserEvaluate(text);
        evaluate.setUserDate(LocalDateTime.now());

        // 判断对方是否评分，如果评分过，关闭评分
        if(evaluate.getCourierScore() != null) {
            evaluate.setHasOpen(false);
        }

        // 设置对方的ID
        if(StringUtils.isBlank(evaluate.getCourierId())) {
            OrderInfo orderInfo = orderInfoService.getById(orderId);
            evaluate.setCourierId(orderInfo.getCourierId());
        }

        if(!this.retBool(orderEvaluateMapper.updateById(evaluate))) {
            transactionManager.rollback(status);
            return ResponseResult.failure(ResponseErrorCodeEnum.ORDER_EVALUATE_ERROR);
        }

        /* 更新配送员评分 */
        if(!userEvaluateService.updateEvaluate(orderId, score, SysRoleEnum.COURIER)) {
            transactionManager.rollback(status);
            return ResponseResult.failure(ResponseErrorCodeEnum.SCORE_UPDATE_ERROR);
        }

        transactionManager.commit(status);
        return ResponseResult.success();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public ResponseResult courierEvaluate(String orderId, String courierId, double score, String text) {
        DefaultTransactionDefinition definition = new DefaultTransactionDefinition();
        TransactionStatus status = transactionManager.getTransaction(definition);

        // 校验评分
        if(!isLegalScore(score)) {
            return ResponseResult.failure(ResponseErrorCodeEnum.EVALUATE_SCORE_ERROR);
        }

        OrderEvaluate evaluate = super.getById(orderId);

        // 未开启评分
        if(!evaluate.getHasOpen()) {
            return ResponseResult.failure(ResponseErrorCodeEnum.ORDER_NOT_OPEN_EVALUATE);
        }
        // 是否有权限
        if(!orderInfoService.isCourierOrder(orderId, courierId)) {
            return ResponseResult.failure(ResponseErrorCodeEnum.NO_PERMISSION);
        }
        // 已评分
        if(evaluate.getCourierScore() != null) {
            return ResponseResult.failure(ResponseErrorCodeEnum.ORDER_ALREADY_EVALUATE);
        }

        // text超过最大
        if(StringUtils.isNotBlank(text) && text.length() > 255) {
            return ResponseResult.failure(ResponseErrorCodeEnum.STR_LENGTH_OVER, new Object[]{"评价", 255});
        }

        /* 订单评分 */
        evaluate.setCourierId(courierId);
        evaluate.setCourierScore(new BigDecimal(score));
        evaluate.setCourierEvaluate(text);
        evaluate.setCourierDate(LocalDateTime.now());

        // 判断对方是否评分，如果评分过，关闭评分
        if(evaluate.getUserScore() != null) {
            evaluate.setHasOpen(false);
        }

        // 设置对方的ID
        if(StringUtils.isBlank(evaluate.getUserId())) {
            OrderInfo orderInfo = orderInfoService.getById(orderId);
            evaluate.setUserId(orderInfo.getUserId());
        }

        if(!this.retBool(orderEvaluateMapper.updateById(evaluate))) {
            transactionManager.rollback(status);
            return ResponseResult.failure(ResponseErrorCodeEnum.ORDER_EVALUATE_ERROR);
        }

        /* 更新普通用户的评分 */
        if(!userEvaluateService.updateEvaluate(orderId, score, SysRoleEnum.USER)) {
            transactionManager.rollback(status);
            return ResponseResult.failure(ResponseErrorCodeEnum.SCORE_UPDATE_ERROR);
        }

        transactionManager.commit(status);
        return ResponseResult.success();
    }

    @Override
    public int countEvaluate(String userId, SysRoleEnum roleEnum) {
        QueryWrapper<OrderEvaluate> wrapper = new QueryWrapper<>();

        if(roleEnum == SysRoleEnum.USER) {
            wrapper.eq("user_id", userId).isNotNull("courier_evaluate");
        } else if(roleEnum == SysRoleEnum.COURIER) {
            wrapper.eq("courier_id", userId).isNotNull("user_evaluate");
        } else {
            return -1;
        }

        return orderEvaluateMapper.selectCount(wrapper);
    }

    private boolean isLegalScore(double score) {
        return score >= 0 && score <= 10;
    }
}
