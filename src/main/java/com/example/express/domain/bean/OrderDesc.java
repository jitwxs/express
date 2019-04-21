package com.example.express.domain.bean;

import com.baomidou.mybatisplus.annotation.*;
import com.example.express.domain.enums.OrderStatusEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 订单详情
 * @date 2019年04月16日 23:08
 */
@Data
public class OrderDesc {
    /**
     * 订单ID
     */
    @TableId(type = IdType.ID_WORKER)
    private Long orderId;
    /**
     * 代取人ID
     */
    private String courierId;
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

    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createDate;

    @TableField(fill = FieldFill.UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateDate;
}
