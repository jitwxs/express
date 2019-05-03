package com.example.express.domain.vo.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 普通用户端，OrderVO
 * @author jitwxs
 * @date 2019年04月24日 22:24
 */
@Data
@Builder
public class UserOrderVO implements Serializable {
    /**
     * 订单号
     */
    private String id;
    /**
     * 快递单号
     */
    private String odd;
    /**
     * 快递公司
     */
    private String company;
    /**
     * 支付状态
     */
    private Integer paymentStatus;
    /**
     * 支付金额
     */
    private String payment;
    /**
     * 订单状态
     */
    private Integer orderStatus;
    /**
     * 删除原因
     */
    private Integer deleteType;
    /**
     * 能否评分，1可以，0可以
     */
    private String canScore;
    /**
     * 下单时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createDate;
}
