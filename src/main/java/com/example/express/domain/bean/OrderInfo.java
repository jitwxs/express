package com.example.express.domain.bean;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 订单信息
 * @date 2019年04月16日 23:06
 */
@Data
public class OrderInfo {
    @TableId(type = IdType.ID_WORKER)
    private Long id;

    private String userId;
    /**
     * 快递单号
     */
    private String odd;
    /**
     * 快递公司
     */
    private String company;
    /**
     * 收货地址
     */
    private String recAddress;

    @TableLogic
    private Integer hasDelete;

    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createDate;

    @TableField(fill = FieldFill.UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateDate;
}
