package com.example.express.domain.enums;

import com.baomidou.mybatisplus.core.enums.IEnum;
import lombok.Getter;

import java.util.Arrays;

/**
 * 订单状态枚举
 * @date 2019年04月16日 23:21
 */
@Getter
public enum  OrderStatusEnum  implements IEnum<Integer> {
    /**
     * 等待接单
     */
    WAIT_DIST("等待接单", 1),

    /**
     * 派送中
     */
    TRANSPORT("派送中", 2),

    /**
     * 订单完成
     */
    COMPLETE("订单完成", 3),

    /**
     * '订单异常
     */
    ERROR("订单异常", 4);

    private String name;
    private int status;

    OrderStatusEnum(String name, int status) {
        this.name = name;
        this.status = status;
    }

    public static OrderStatusEnum getByStatus(Integer status) {
        return Arrays.stream(values()).filter(e -> e.getStatus() == status).findFirst().orElse(null);
    }

    @Override
    public Integer getValue() {
        return this.status;
    }
}
