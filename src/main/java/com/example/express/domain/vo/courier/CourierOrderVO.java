package com.example.express.domain.vo.courier;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 配送员端，OrderVO
 * @author jitwxs
 * @date 2019年04月24日 22:24
 */
@Data
@Builder
public class CourierOrderVO implements Serializable {
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
     * 收件人
     */
    private String recName;
    /**
     * 收件电话
     */
    private String recTel;
    /**
     * 快递寄达地址
     */
    private String address;
    /**
     * 收货地址
     */
    private String recAddress;
    /**
     * 备注
     */
    private String remark;
    /**
     * 订单状态
     */
    private Integer orderStatus;
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
