package com.example.express.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.express.common.util.CollectionUtils;
import com.example.express.common.util.StringUtils;
import com.example.express.config.AliPayConfig;
import com.example.express.domain.ResponseResult;
import com.example.express.domain.bean.OrderInfo;
import com.example.express.domain.bean.OrderPayment;
import com.example.express.domain.enums.OrderDeleteEnum;
import com.example.express.domain.enums.OrderStatusEnum;
import com.example.express.domain.enums.ResponseErrorCodeEnum;
import com.example.express.domain.enums.SysRoleEnum;
import com.example.express.domain.vo.BootstrapTableVO;
import com.example.express.domain.vo.OrderDescVO;
import com.example.express.domain.vo.OrderVO;
import com.example.express.exception.CustomException;
import com.example.express.mapper.OrderInfoMapper;
import com.example.express.service.DataCompanyService;
import com.example.express.service.OrderInfoService;
import com.example.express.service.OrderPaymentService;
import com.example.express.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OrderInfoServiceImpl extends ServiceImpl<OrderInfoMapper, OrderInfo> implements OrderInfoService {
    @Autowired
    private OrderInfoMapper orderInfoMapper;
    @Autowired
    private OrderPaymentService orderPaymentService;
    @Autowired
    private DataCompanyService dataCompanyService;
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private AliPayConfig aliPayConfig;

    @Override
    public boolean isExistUnfinishedOrder(String userId, SysRoleEnum roleEnum) {
        int count = Integer.MAX_VALUE;
        if(roleEnum == SysRoleEnum.USER) {
            count = orderInfoMapper.selectCount(new QueryWrapper<OrderInfo>()
                    .eq("user_id", userId)
                    .in("status", OrderStatusEnum.WAIT_DIST.getStatus(), OrderStatusEnum.TRANSPORT.getStatus()));
        } else if(roleEnum == SysRoleEnum.COURIER) {
            count = orderInfoMapper.selectCount(new QueryWrapper<OrderInfo>()
                    .eq("courier_id", userId)
                    .in("status", OrderStatusEnum.WAIT_DIST.getStatus(), OrderStatusEnum.TRANSPORT.getStatus()));
        }

        return count != 0;
    }

    @Transactional(rollbackFor = CustomException.class)
    @Override
    public String createOrder(OrderInfo orderInfo, double money, String uid) {
        orderInfo.setOrderStatus(OrderStatusEnum.WAIT_DIST);
        orderInfo.setUserId(uid);

       if(!this.retBool(orderInfoMapper.insert(orderInfo))) {
           throw new CustomException(ResponseErrorCodeEnum.ORDER_CREATE_ERROR);
       }

       String orderId = orderInfo.getId();
        boolean b = orderPaymentService.createAliPayment(orderId, money, aliPayConfig.getSellerId());
        if(!b) {
            throw new CustomException(ResponseErrorCodeEnum.ORDER_PAYMENT_CREATE_ERROR);
        }

        return orderId;
    }

    @Override
    public OrderDescVO getDescVO(String orderId) {
        OrderInfo orderInfo = orderInfoMapper.selectById(orderId);
        if(orderInfo == null) {
            return new OrderDescVO();
        }

        OrderDescVO vo = OrderDescVO.builder()
                .orderId(orderId)
                .odd(orderInfo.getOdd())
                .companyName(dataCompanyService.getByCache(orderInfo.getCompany()).getName())
                .recName(orderInfo.getRecName())
                .recTel(orderInfo.getRecTel())
                .recAddress(orderInfo.getRecAddress())
                .remark(orderInfo.getRemark())
                .orderStatus(orderInfo.getOrderStatus().getName()).build();

        if(StringUtils.isNotBlank(orderInfo.getCourierId())) {
            String courierFrontName = sysUserService.getFrontName(orderInfo.getCourierId());
            vo.setCourierFrontName(courierFrontName);
            vo.setCourierRemark(orderInfo.getCourierRemark());
        }

        OrderPayment payment = orderPaymentService.getById(orderId);
        if(payment != null) {
            vo.setPaymentStatus(payment.getPaymentStatus().getName());
            vo.setPaymentType(payment.getPaymentType().getName());
            vo.setPayment(payment.getPayment().toString());
        }

        return vo;
    }

    @Override
    public boolean isExist(String orderId) {
        return orderInfoMapper.selectById(orderId) != null;
    }

    @Override
    public BootstrapTableVO<OrderVO> pageOrderVO(Page<OrderInfo> page, QueryWrapper<OrderInfo> wrapper) {
        BootstrapTableVO<OrderVO> vo = new BootstrapTableVO<>();

        IPage<OrderInfo> selectPage = orderInfoMapper.selectPage(page, wrapper);
        vo.setTotal(selectPage.getTotal());
        vo.setRows(convert(selectPage.getRecords()));

        return vo;
    }

    @Transactional
    @Override
    public ResponseResult batchDeleteOrder(String[] ids, String id) {
        int success = 0;
        for(String orderId : ids) {
            OrderInfo orderInfo = orderInfoMapper.selectById(orderId);
            if(!id.equals(orderInfo.getUserId())) {
                continue;
            }
            if (orderInfo.getOrderStatus() != OrderStatusEnum.COMPLETE && orderInfo.getOrderStatus() != OrderStatusEnum.ERROR) {
                continue;
            }
            if(manualDelete(orderId, OrderDeleteEnum.MANUAL.getType())) {
                success++;
            }
        }
        int finalSuccess = success;
        Map<String, Integer> count = new HashMap<String, Integer>() {{
           put("success", finalSuccess);
           put("error", ids.length - finalSuccess);
        }};

        return ResponseResult.success(count);
    }

    @Transactional
    @Override
    public ResponseResult batchCancelOrder(String[] ids, String id) {
        int success = 0;
        for(String orderId : ids) {
            OrderInfo orderInfo = orderInfoMapper.selectById(orderId);
            if(!id.equals(orderInfo.getUserId())) {
                continue;
            }
            if (orderInfo.getOrderStatus() != OrderStatusEnum.WAIT_DIST) {
                continue;
            }
            if(manualDelete(orderId, OrderDeleteEnum.CANCEL.getType())) {
                success++;
            }
        }
        int finalSuccess = success;
        Map<String, Integer> count = new HashMap<String, Integer>() {{
            put("success", finalSuccess);
            put("error", ids.length - finalSuccess);
        }};

        return ResponseResult.success(count);
    }

    @Override
    public boolean manualDelete(String orderId, Integer deleteType) {
        return orderInfoMapper.manualDelete(orderId, deleteType);
    }

    private List<OrderVO> convert(List<OrderInfo> infos) {
        if(CollectionUtils.isListEmpty(infos)) {
            return Collections.emptyList();
        }
        return infos.stream().map(this::convert).collect(Collectors.toList());
    }

    private OrderVO convert(OrderInfo info) {
        OrderPayment payment = orderPaymentService.getById(info.getId());

        OrderVO vo = OrderVO.builder()
                .id(info.getId())
                .odd(info.getOdd())
                .name(info.getRecName())
                .tel(info.getRecTel())
                .orderStatus(info.getOrderStatus().getStatus())
                .paymentStatus(payment.getPaymentStatus().getStatus())
                .createDate(info.getCreateDate()).build();
        if(info.getCompany() != null) {
            vo.setCompany(dataCompanyService.getByCache(info.getCompany()).getName());
        }

        return vo;
    }
}
