package com.example.express.domain.enums;

import com.baomidou.mybatisplus.core.enums.IEnum;
import lombok.Getter;

import java.util.Arrays;

/**
 * 反馈类型枚举
 * @date 2019年04月17日 0:11
 */
@Getter
public enum FeedbackTypeEnum implements IEnum<Integer> {
    ORDER(1, "订单反馈"),
    OPINION(2, "意见反馈"),
    BUG(3, "BUG反馈");

    private int type;

    private String name;

    FeedbackTypeEnum(int type, String name) {
        this.type = type;
        this.name = name;
    }

    @Override
    public Integer getValue() {
        return this.type;
    }

    public static FeedbackTypeEnum getByType(int type) {
        return Arrays.stream(values()).filter(e -> e.getType() == type).findFirst().orElse(null);
    }
}
