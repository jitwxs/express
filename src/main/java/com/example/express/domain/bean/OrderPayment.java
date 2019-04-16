package com.example.express.domain.bean;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.example.express.domain.enums.PaymentStatusEnum;
import com.example.express.domain.enums.PaymentTypeEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 订单支付
 * @date 2019年04月16日 23:10
 */
@Data
public class OrderPayment {
    @TableId
    private Long orderId;
    /**
     * 订单状态
     */
    @TableField("status")
    private PaymentStatusEnum paymentStatus;
    /**
     * 支付方式
     */
    @TableField("type")
    private PaymentTypeEnum paymentType;
    /**
     * 支付金额
     */
    private BigDecimal payment;
    /**
     * 支付流水号
     */
    private String paymentId;
    /**
     * 收款方
     */
    private String seller;
    /**
     * 备注
     */
    private String remark;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createDate;

    @TableField(update = "now()")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateDate;
}
