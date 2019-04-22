package com.example.express.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.express.domain.bean.OrderInfo;
import com.example.express.domain.enums.OrderStatusEnum;
import com.example.express.domain.enums.SysRoleEnum;
import com.example.express.mapper.OrderInfoMapper;
import com.example.express.service.OrderInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderInfoServiceImpl extends ServiceImpl<OrderInfoMapper, OrderInfo> implements OrderInfoService {
    @Autowired
    private OrderInfoMapper orderInfoMapper;

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
}
