package com.example.express.domain.enums;

import com.baomidou.mybatisplus.core.enums.IEnum;
import lombok.Getter;

import java.util.Arrays;

/**
 * 支付状态枚举
 * @date 2019年04月16日 23:15
 */
@Getter
public enum PaymentStatusEnum implements IEnum<Integer> {
    /**
     * 等待支付
     */
    WAIT_BUYER_PAY("等待支付", "WAIT_BUYER_PAY",1),

    /**
     * 未付款交易超时关闭，或支付完成后全额退款
     * （1)订单已创建，但用户未付款，调用关闭交易接口
     * （2）付款成功后，订单金额已全部退款【如果没有全部退完，仍是TRADE_SUCCESS状态】
     */
    TRADE_CLOSED("支付关闭", "TRADE_CLOSED",2),

    /**
     * 交易支付成功
     * （1）用户付款成功
     */
    TRADE_SUCCESS("支付成功", "TRADE_SUCCESS",3),

    /**
     * 支付结束，不可退款
     * （1）退款日期超过可退款期限后
     */
    TRADE_FINISHED("支付结束", "TRADE_FINISHED",4);

    private String name;
    private int status;
    private String desc;

    PaymentStatusEnum(String name, String desc, int status) {
        this.name = name;
        this.status = status;
        this.desc = desc;
    }

    public static PaymentStatusEnum getByStatus(Integer status) {
        return Arrays.stream(values()).filter(e -> e.getStatus() == status).findFirst().orElse(null);
    }

    public static PaymentStatusEnum getByDesc(String desc) {
        return Arrays.stream(values()).filter(e -> e.getDesc().equals(desc)).findFirst().orElse(null);
    }

    @Override
    public Integer getValue() {
        return this.status;
    }
}
