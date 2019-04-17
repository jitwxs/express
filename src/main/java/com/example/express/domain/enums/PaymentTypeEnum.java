package com.example.express.domain.enums;

import com.baomidou.mybatisplus.core.enums.IEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 支付方式枚举
 * @date 2019年04月16日 23:15
 */
@Getter
public enum PaymentTypeEnum implements IEnum<Integer> {
    /**
     * 支付宝支付
     */
    AliPay(1, "支付宝");

    private int type;

    private String name;

    PaymentTypeEnum(int type, String name) {
        this.type = type;
        this.name = name;
    }

    @Override
    public Integer getValue() {
        return this.type;
    }
}
