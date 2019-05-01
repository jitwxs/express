package com.example.express.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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
import com.example.express.domain.vo.CourierOrderVO;
import com.example.express.domain.vo.OrderDescVO;
import com.example.express.domain.vo.UserOrderVO;
import com.example.express.mapper.OrderInfoMapper;
import com.example.express.service.DataCompanyService;
import com.example.express.service.OrderInfoService;
import com.example.express.service.OrderPaymentService;
import com.example.express.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.HashMap;
import java.util.Map;

/**
 * @author oker
 */
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
    @Autowired
    private DataSourceTransactionManager transactionManager;

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
                    .in("status",  OrderStatusEnum.TRANSPORT.getStatus()));
        }

        return count != 0;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public ResponseResult createOrder(OrderInfo orderInfo, double money, String uid) {
        DefaultTransactionDefinition definition = new DefaultTransactionDefinition();
        TransactionStatus status = transactionManager.getTransaction(definition);

        orderInfo.setOrderStatus(OrderStatusEnum.WAIT_DIST);
        orderInfo.setUserId(uid);

       if(!this.retBool(orderInfoMapper.insert(orderInfo))) {
           transactionManager.rollback(status);
           return ResponseResult.failure(ResponseErrorCodeEnum.ORDER_CREATE_ERROR);
       }

       String orderId = orderInfo.getId();
        boolean b = orderPaymentService.createAliPayment(orderId, money, aliPayConfig.getSellerId());
        if(!b) {
            transactionManager.rollback(status);
            return ResponseResult.failure(ResponseErrorCodeEnum.ORDER_PAYMENT_CREATE_ERROR);
        }

        transactionManager.commit(status);
        return ResponseResult.success(orderId);
    }

    @Override
    public OrderDescVO getDescVO(String orderId) {
        OrderInfo orderInfo = orderInfoMapper.selectById(orderId);
        if(orderInfo == null) {
            return null;
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
            vo.setPaymentId(payment.getPaymentId());
        }

        return vo;
    }

    @Override
    public boolean isExist(String orderId) {
        return orderInfoMapper.selectById(orderId) != null;
    }

    @Override
    public BootstrapTableVO<UserOrderVO> pageUserOrderVO(Page<UserOrderVO> page, String selectSql, int isDelete) {
        BootstrapTableVO<UserOrderVO> vo = new BootstrapTableVO<>();

        IPage<UserOrderVO> selectPage = orderInfoMapper.pageUserOrderVO(page, selectSql, isDelete);
        vo.setTotal(selectPage.getTotal());
        vo.setRows(selectPage.getRecords());

        return vo;
    }

    @Override
    public BootstrapTableVO<CourierOrderVO> pageCourierOrderVO(Page<CourierOrderVO> page, String sql) {
        BootstrapTableVO<CourierOrderVO> vo = new BootstrapTableVO<>();

        IPage<CourierOrderVO> selectPage = orderInfoMapper.pageCourierOrderVO(page, sql);
        vo.setTotal(selectPage.getTotal());

        vo.setRows(selectPage.getRecords());

        return vo;
    }

    @Override
    public ResponseResult batchDeleteOrder(String[] ids, String userId) {
        int success = 0;
        for(String orderId : ids) {
            OrderInfo orderInfo = orderInfoMapper.selectById(orderId);
            if(!userId.equals(orderInfo.getUserId())) {
                continue;
            }
            if (orderInfo.getOrderStatus() != OrderStatusEnum.COMPLETE && orderInfo.getOrderStatus() != OrderStatusEnum.ERROR) {
                continue;
            }
            if(manualDelete(orderId, 1, OrderDeleteEnum.MANUAL.getType())) {
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
    public ResponseResult batchCancelOrder(String[] ids, String userId) {
        int success = 0;
        for(String orderId : ids) {
            OrderInfo orderInfo = orderInfoMapper.selectById(orderId);
            if(!userId.equals(orderInfo.getUserId())) {
                continue;
            }
            if (orderInfo.getOrderStatus() != OrderStatusEnum.WAIT_DIST) {
                continue;
            }
            if(manualDelete(orderId, 1, OrderDeleteEnum.CANCEL.getType())) {
                success++;
            }
        }
        int finalSuccess = success;
        Map<String, Integer> count = new HashMap<String, Integer>(16) {{
            put("success", finalSuccess);
            put("error", ids.length - finalSuccess);
        }};

        return ResponseResult.success(count);
    }

    @Override
    public ResponseResult batchRollback(String[] ids, String userId) {
        int success = 0;
        for(String orderId : ids) {
            OrderInfo orderInfo = orderInfoMapper.selectByIdIgnoreDelete(orderId);
            if(!userId.equals(orderInfo.getUserId())) {
                continue;
            }
            if(manualDelete(orderId, 0, OrderDeleteEnum.CANCEL.getType())) {
                success++;
            }
        }
        int finalSuccess = success;
        Map<String, Integer> count = new HashMap<String, Integer>(16) {{
            put("success", finalSuccess);
            put("error", ids.length - finalSuccess);
        }};

        return ResponseResult.success(count);
    }

    @Override
    public ResponseResult batchAcceptOrder(String[] ids, String userId) {
        int success = 0;
        for(String orderId: ids) {
            OrderInfo orderInfo = orderInfoMapper.selectById(orderId);
            orderInfo.setCourierId(userId);
            orderInfo.setOrderStatus(OrderStatusEnum.TRANSPORT);
            if(this.retBool(orderInfoMapper.updateById(orderInfo))) {
                success++;
            }
        }

        int finalSuccess = success;
        Map<String, Integer> count = new HashMap<String, Integer>(16) {{
            put("success", finalSuccess);
            put("error", ids.length - finalSuccess);
        }};

        return ResponseResult.success(count);
    }

    @Override
    public ResponseResult handleOrder(String orderId, OrderStatusEnum targetStatus, String remark) {
        OrderInfo orderInfo = getById(orderId);
        if(orderInfo == null) {
            return ResponseResult.failure(ResponseErrorCodeEnum.ORDER_NOT_EXIST);
        }
        if(orderInfo.getOrderStatus() != OrderStatusEnum.TRANSPORT) {
            return ResponseResult.failure(ResponseErrorCodeEnum.OPERATION_NOT_SUPPORT);
        }

        orderInfo.setOrderStatus(targetStatus);
        if(StringUtils.isNotBlank(remark)) {
            orderInfo.setCourierRemark(remark);
        }
        if(this.retBool(orderInfoMapper.updateById(orderInfo))) {
            return ResponseResult.success();
        } else {
            return ResponseResult.failure(ResponseErrorCodeEnum.OPERATION_ERROR);
        }
    }

    @Override
    public boolean manualDelete(String orderId, int hasDelete, int deleteType) {
        return orderInfoMapper.manualDelete(orderId, hasDelete, deleteType);
    }

    @Override
    public boolean isUserOrder(String orderId, String userId) {
        OrderInfo info = getById(orderId);
        if(info == null) {
            return false;
        }
        return info.getUserId().equals(userId);
    }
}
