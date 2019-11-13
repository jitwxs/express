package com.example.express.domain.bean;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单评价表
 * @author jitwxs
 * @date 2019年05月03日 23:40
 */
@Data
public class OrderEvaluate {
    /**
     * 订单ID
     */
    @TableId(type = IdType.INPUT)
    private String id;
    /**
     * 评论是否开启（1：开启；0：关闭）
     */
    private Boolean hasOpen;
    /**
     * 用户ID
     */
    private String userId;
    /**
     * 用户评分
     */
    private BigDecimal userScore;
    /**
     * 用户评价
     */
    private String userEvaluate;
    /**
     * 用户评价时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime userDate;
    /**
     * 用户ID
     */
    private String courierId;
    /**
     * 用户评分
     */
    private BigDecimal courierScore;
    /**
     * 用户评价
     */
    private String courierEvaluate;

    @Version
    private Integer version;

    /**
     * 用户评价时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime courierDate;

    @TableField(fill = FieldFill.UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateDate;
}
