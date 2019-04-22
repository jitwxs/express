package com.example.express.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.express.config.AliPayConfig;
import com.example.express.domain.bean.OrderInfo;
import com.example.express.domain.enums.OrderStatusEnum;
import com.example.express.domain.enums.ResponseErrorCodeEnum;
import com.example.express.domain.enums.SysRoleEnum;
import com.example.express.exception.CustomException;
import com.example.express.mapper.OrderInfoMapper;
import com.example.express.service.OrderInfoService;
import com.example.express.service.OrderPaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderInfoServiceImpl extends ServiceImpl<OrderInfoMapper, OrderInfo> implements OrderInfoService {
    @Autowired
    private OrderInfoMapper orderInfoMapper;
    @Autowired
    private OrderPaymentService orderPaymentService;
    @Autowired
    private AliPayConfig aliPayConfig;

    @Override
    public boolean isExistUnfinishedOrder(String userId, SysRoleEnum roleEnum) {
        int count = Integer.MAX_VALUE;
        if(roleEnum == SysRoleEnum.USER) {
            count = orderInfoMapper.selectCount(new QueryWrapper<OrderInfo>()
                    .eq("user_id", userId)
                    .in("status", OrderStatusEnum.WAIT_DIST.getIndex(), OrderStatusEnum.TRANSPORT.getIndex()));
        } else if(roleEnum == SysRoleEnum.COURIER) {
            count = orderInfoMapper.selectCount(new QueryWrapper<OrderInfo>()
                    .eq("courier_id", userId)
                    .in("status", OrderStatusEnum.WAIT_DIST.getIndex(), OrderStatusEnum.TRANSPORT.getIndex()));
        }

        return count != 0;
    }

    @Transactional(rollbackFor = CustomException.class)
    @Override
    public long createOrder(OrderInfo orderInfo, double money, String uid) {
        orderInfo.setOrderStatus(OrderStatusEnum.WAIT_DIST);
        orderInfo.setUserId(uid);

       if(!this.retBool(orderInfoMapper.insert(orderInfo))) {
           throw new CustomException(ResponseErrorCodeEnum.ORDER_CREATE_ERROR);
       }

       Long orderId = orderInfo.getId();
        boolean b = orderPaymentService.createAliPayment(orderId, money, aliPayConfig.getSellerId());
        if(!b) {
            throw new CustomException(ResponseErrorCodeEnum.ORDER_PAYMENT_CREATE_ERROR);
        }

        return orderId;
    }
}
