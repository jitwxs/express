package com.example.express.domain.bean;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.example.express.domain.enums.OrderStatusEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * 订单详情
 * @date 2019年04月16日 23:08
 */
@Data
public class OrderDesc {
    /**
     * 订单ID
     */
    @TableId
    private Long orderId;
    /**
     * 代取人ID
     */
    private Long courierId;
    /**
     * 订单状态
     */
    @TableField("status")
    private OrderStatusEnum orderStatus;
    /**
     * 代取人备注
     */
    private String courierRemark;

    @TableLogic
    private Integer hasDelete;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createDate;

    @TableField(update = "now()")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateDate;
}
