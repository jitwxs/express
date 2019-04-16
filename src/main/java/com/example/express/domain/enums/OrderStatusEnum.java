package com.example.express.domain.enums;

import com.baomidou.mybatisplus.core.enums.IEnum;
import lombok.Getter;

/**
 * 订单状态枚举
 * @date 2019年04月16日 23:21
 */
@Getter
public enum  OrderStatusEnum  implements IEnum<Integer> {
    /**
     * 等待派送
     */
    WAIT_DIST("等待派送", 1),

    /**
     * 派送中
     */
    TRANSPORT("派送中", 2),

    /**
     * 订单完成
     */
    COMPLTE("订单完成", 3),

    /**
     * '订单异常
     */
    ERROR("订单异常", 4);

    private String name;
    private int index;

    OrderStatusEnum(String name, int index) {
        this.name = name;
        this.index = index;
    }

    @Override
    public Integer getValue() {
        return this.index;
    }
}
