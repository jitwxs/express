package com.example.express.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.express.domain.bean.OrderPayment;
import com.example.express.domain.enums.PaymentStatusEnum;

import java.util.Map;

public interface OrderPaymentService extends IService<OrderPayment> {
    /**
     * 创建支付宝订单
     * @author jitwxs
     * @since 2018/6/11 17:26
     */
    boolean createAliPayment(long orderId, double money, String sellerId);

    boolean validAlipay(Map<String,String> params) throws Exception;

    /**
     * 改变订单状态
     * @param tradeNo 第三方交易号【仅在TRADE_SUCCESS时有效】
     * @author jitwxs
     * @since 2018/6/4 22:42
     */
    boolean updateStatus(long orderId, PaymentStatusEnum status, String... tradeNo);
}
